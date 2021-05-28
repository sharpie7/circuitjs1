# Internals

Here is a brief description of how we simulate a circuit.
See the book Electronic Circuit and System Simulation Methods by
Pillage, et al for more information.

Basically, we create a matrix.  Each row in the matrix represents
a node in the circuit.  The matrix equation looks like A x = B
where x is a vector containing the voltages of each node.  B
represents the net current in or out of each node, and should be
all zeroes by Kirchoff's current law, unless there is a current
source somewhere.

If you have a resistor, you want Vb-Va=IR or Vb/R - Va/R = I.  So
the net current out of node b and into node a depends on the voltage
Vb and Va.  You add matrix elements -1/R and 1/R at rows a and b
and columns a and b to reflect this.  (See CirSim.stampResistor()).
Then after adding all resistors to the matrix, you solve for x, and
that gives you the voltage at each node, and from that you can
derive the currents through each element.  We leave out node 0, the
ground node, because the matrix would be singular otherwise (there
would be an infinite number of solutions, because all nodes can be
shifted up or down by the same voltage).

To implement a current source of current I, we simply subtract I from
row a of the right side vector, and add I to row b.  This
represents a flow of current I from a to b. (CirSim.stampCurrentSource())

We need to add additional rows to the matrix to implement voltage
sources.  Each voltage source needs an additional row to enforce
the voltage constraint, and also an additional element in x (and
an extra matrix column) to solve for the current (since we have no other
way to obtain it).  For a voltage source with voltage Vs across
nodes a and b, the voltage constraint equation is Vb-Va = Vs.  To express
that as a row in the matrix, you add matrix elements 1
and -1 in the extra row at columns b and a, and set the corresponding
element of the right side (B) to Vs.  Also, to represent a flow of
current I from node a to b, we add matrix elements 1 to -1 in rows
a and b in the extra column.  (CirSim.stampVoltageSource())
Now when solving for x, we get the voltage at each node, and the
current through each voltage source.

When simulating inductors, the current state changes over time.
We use a small timestep value to step through time iteratively to
simulate the circuit.  We treat an inductor as a current source in
parallel with a resistor.  The current source has current equal to
the current through the inductor at a particular time.  The resistor
represents resistance to changes in current, and the resistance
value is proportional to the inductance.  So for each time step,
we solve the equation, get the new current, and then update
the current source value.  (We just need to change the right
side for this, not the matrix.)

This is numerical integration, and there are several ways to do it.
There's forward Euler which we don't use.  There's backward Euler which is 
more stable than forward Euler.  And there is trapezoidal which
is more accurate than backward Euler but less stable.  You can select
either trapezoidal or backward Euler.  Trapezoidal will give you
better accuracy for something like an LRC circuit or a filter, but
backward Euler will give you better stability (much less oscillation)
if an inductor is suddenly switched on or off.  To implement these
in our inductors, we simply choose different values for the resistor
and the current through the current source, depending on which
method is being used.

To simulate capacitors, you could simulate it as a voltage source
in series with a resistor.  But this would require two extra rows
in the matrix.  Instead, we simulate it as a current source in
parallel with a resistor.  The resistor value is inversely proportional
to the capacitance, because large capacitors can store more charge
(accept more current flow) without a large change in voltage.  The
current from the current source flows in a loop through the resistor
and this simulates the charge on the capacitor.  This current changes
after each timestep, like the inductor.  After each step, we get
the new voltage and current and update the current source appropriately.

For nonlinear devices the matrix must be solved iteratively.  For example,
a diode has current that is an exponential function of the voltage.
For that we start with a given voltage and linearize the diode's
response at that point, so we can represent it in the matrix.  We find
the tangent line to the diode's response curve at that voltage,
and that line can be expressed as a resistance (depending on the
slope of the line) and a current source (depending on its height).
After solving the matrix, we get a new voltage across the diode, and we
compare it with the old voltage to see if it's nearly the same.  If
not, then we have to calculate a new linearization and create a new
matrix.  This continues until it converges on a value that is nearly
the same as the previous one.

When simulating diodes and transistors, we have to be careful to limit the
changes in voltage at each iteration.  Since the response is an
exponential function, we could easily end up with an enormous current.
The linearization could be too large to represent accurately in the
matrix.

Nonlinear devices require iteration, which requires us to create a new
matrix and fully solve it at least once per timestep.  If there are no
nonlinear devices, we don't need to do this.  The matrix doesn't
change, only the right side.  We can partially solve this matrix
beforehand, by doing an LU factorization.  This saves a lot of time
for large linear circuits, so we do this whenever possible.
We call CircuitElm.nonLinear() for each element when analyzing the circuit
to see if we need to do the extra work.

Whenever the circult changes, we call analyzeCircuit().  When analyzing a
circuit, we call stamp() for each CircuitElm to
create the matrix.  This creates the circuit elements that don't
change.  Then for each time step, we call doStep().  This modifies
the right side as needed (for linear elements) or modifies the
matrix further (for nonlinear elements).  doStep() should also
check to see if we are within convergence limits and set the
converged flag to false if not.

One of the first steps in analyzing the circuit is building the node list.  Every
circuit element is connected to one or more nodes, which are connection
points in the circuit.  We have to allocate a node for each point and
assign it a number.  We also allocate internal nodes, which are used by some
circuit elements that need an extra node in the matrix to determine their
internal state.  For example, a tri-state buffer is implemented by a voltage
source in series with a resistor.  So we need an internal node, in the matrix
but not shown on screen, so they can be connected in series.

We used to allocate a node for each end of a wire, too, and
implement a wire as a zero-valued voltage source.  But we stopped doing that
because it is very inefficient; every wire required two extra rows
in the matrix.  Instead, all points connected by wires are
considered the same node.  The calculateWireClosure() method figures this out
and builds a map to determine which nodes are connected.

We also have the calcWireInfo() method to calculate the info we need to
generate wire currents.  The wire currents are not necessarily to
simulate the circuit, but they are necessary to display it, since this
simulator shows an animation of current flowing through the circuit.
When wires were implemented as voltage sources,
figuring out the current was easy.  We got it by solving the matrix, as
described above.  But with points connected by wires being considered the
same node, we don't get the current by solving the matrix.  All we know is
the voltage.  So we have to get the current from the elements they are
connected to.  If a wire is connected in series with two resistors, we know the
current is the same as the resistor currents.  We use getCurrentIntoNode()
to retrieve this.  If one side of a wire is connected to multiple resistors,
then we can add up all the resistor currents to
get the current through the wire.  If a wire is only connected to other wires,
then we have to wait until those wires' current is calculated some other way.
When we have enough information to determine the current on one side, then
we can calculate the current.  So we have to determine what order to process
the wires in, which side to look at, and which elements to get the current
from.  This is basically like solving a matrix equation, except that
the steps are predetermined beforehand.

After stamping all the circuit elements into the matrix, we simplify the
matrix (in simplifyMatrix()).  This was an important step before we removed wires
from the matrix; now it's less important.  Since the number of steps required to solve a
matrix is proportional to n^3, where n is the number of rows, it is worth
a lot of effort to reduce the size of the matrix as much as possible.  So
we build a new matrix by removing rows that are trivial.  For example,
if we find a row with only one nonzero element, then we can remove that
row and the nonzero column.  We can solve that row without looking at the
rest of the matrix.  This can get tricky, because if the circuit
is nonlinear, we still might modify the matrix after doing this.  So we
have to build a mapping to map the old row numbers to the new ones.  We
also have to keep track of which rows may be filled in later, so we know
to leave those rows alone.  This is what circuitRowInfo[] is for.
