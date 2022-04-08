## Contents
- Foreword
- Internals
- Adding New Elements
- Program Loop

# Foreword

The original design and implementation of the simulation is based on the book **_Electronic Circuit and System Simulation Methods_ (Pillage, L., Rohrer, R., &amp; Visweswariah, C. (1999))**.

The core part of the simulation uses **Modified Nodal Analysis** to determine the voltage of each node in a given circuit, as well as the current of select elements. You can find a detailed introduction to modified nodal analysis [here (Cheever, E., Swarthmore College, (2005))](https://lpsa.swarthmore.edu/Systems/Electrical/mna/MNA1.html).

# Internals

The simulation constructs and solves the following matrix equation:

**X = A⁻¹B**

Where 
- `A` is a _square matrix_ containing one row for each _node_ in the circuit (each connection between two or more elements) and one row for each _independent voltage source_. The contents of this matrix describe how the elements of the circuit are connected, expressed as _admittance_.
- `B` is a _column vector_ also containing one entry for each node in the circuit and one entry for each independent voltage source. The entries associated with circuit nodes are usually zero, unless an _independent current source_ is present. The entries associated with the voltage sources contain the voltage of those sources.
- `X` is a column vector that will contain, after solving: one entry for each node in the circuit containing the voltage at that node and one entry for each independent voltage source containing the current across the voltage source.
- `A⁻¹` means the inversion of the matrix `A` via _LU decomposition_ (also called _LU factorization_).
- `A⁻¹B` means multiplying the result of the inversion of the `A` matrix with the column vector `B`.

If you have a resistor, you want Vb-Va=IR or Vb/R - Va/R = I.  So
the net current out of node b and into node a depends on the voltage
Vb and Va.  You add matrix elements -1/R and 1/R at rows a and b
and columns a and b to reflect this.  (See `CirSim.stampResistor()`).
Then after adding all resistors to the matrix, you solve for x, and
that gives you the voltage at each node, and from that you can
derive the currents through each element.  We leave out node 0, the
ground node, because the matrix would be singular otherwise (there
would be an infinite number of solutions, because all nodes can be
shifted up or down by the same voltage).

To implement a current source of current I, we simply subtract I from
row a of the right side vector, and add I to row b.  This
represents a flow of current I from a to b. (`CirSim.stampCurrentSource()`)

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
a and b in the extra column.  (`CirSim.stampVoltageSource()`)
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
We call `CircuitElm.nonLinear()` for each element when analyzing the circuit
to see if we need to do the extra work.

Whenever the circult changes, we call `analyzeCircuit()`.  When analyzing a
circuit, we call `stamp()` for each `CircuitElm` to
create the matrix.  This creates the circuit elements that don't
change.  Then for each time step, we call `doStep()`.  This modifies
the right side as needed (for linear elements) or modifies the
matrix further (for nonlinear elements).  `doStep()` should also
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
considered the same node.  The `calculateWireClosure()` method figures this out
and builds a map to determine which nodes are connected.

We also have the `calcWireInfo()` method to calculate the info we need to
generate wire currents.  The wire currents are not necessarily to
simulate the circuit, but they are necessary to display it, since this
simulator shows an animation of current flowing through the circuit.
When wires were implemented as voltage sources,
figuring out the current was easy.  We got it by solving the matrix, as
described above.  But with points connected by wires being considered the
same node, we don't get the current by solving the matrix.  All we know is
the voltage.  So we have to get the current from the elements they are
connected to.  If a wire is connected in series with two resistors, we know the
current is the same as the resistor currents.  We use `getCurrentIntoNode()`
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
matrix (in `simplifyMatrix()`).  This was an important step before we removed wires
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
to leave those rows alone.  This is what `circuitRowInfo[]` is for.

# Adding New Elements

To add a new element, you do the following:

* pick a dump type by going to `createCe()` in CirSim.java and picking an unused number.  I wouldn't
pick the next higher number because that's what I do, and you don't want to pick the same one.
Pick an unused number somewhere in the 200 or 300 range.
* add an entry to `createCe()` for your new dump type
* add an entry to `constructElement()` (also in CirSim.java) for your class name
* add your new element to the menu somewhere in `composeMainMenu()`
* create a new class deriving from `CircuitElm`, or from another element if that makes more sense.  Pick
an element that is most similar and copy the implementation.  If you are making a chip, then derive
from `ChipElm`.  If you want to implement your element using other elements, then derive from
`CompositeElm`.
* implement the first constructor, the one that takes two integers.  This one is used when creating a new
element from the user interface.  The second one that takes a StringTokenizer is used when loading from
a file (and from links, doing recovery, performing duplicate, paste, and undo, etc.)  Start with the
first one; the second one can come later.
* change `getDumpType()` to match the dump type you picked
* change `getPostCount()` to have the correct number of posts (aka the terminals)
* implement `setPoints()` to lay out the element, calculating the locations of all the posts and
all the intermediate points.  Call `super.setPoints()` first.  Then `point1` (or x, y) will the the location of the starting location
where the user started dragging, and `point2` (or x2, y2) will be the location where the user stopped dragging.
These are also the first two posts by default, but they don't have to be.  dn will be the total
length.  See `CircuitElm.setPoints()`.  For simple two terminal elements, you can call `CircuitElm.calcLeads()`
to simplify this.
* implement `getPost()` to return the posts
* override `drag()` if there's something unusual you want, like an element that can only be horizontal or vertical.
* implement `draw()` to draw the element.  Also call `drawPosts()` to draw the posts.
* also in `draw()`, calculate the bounding box by calling the various forms of `setBbox()` and `adjustBbox()`.  If you
forget to do this, then the element will not be selectable with the mouse.
* test out your `draw()` method.  Make sure all the nodes are connected to something with a well-defined
voltage, or else you may
get a matrix error or other weird behavior if you haven't implemented `stamp()` yet, and if you are using
the default `getConnection()` (see below).
* implement `getInternalNodeCount()` if your element needs internal nodes.
* implement `getVoltageSourceCount()` if your element needs voltage sources.  If you just need one then
your voltage source id will be stored in the voltSource member.  If you need more than one then you need
to implement `setVoltageSource()`.  If you are making a digital chip, each output is a voltage source,
so this is equal to the number of outputs.
* if you are making a chip, implement `setupPins()`, `getChipName()`, and `execute()`.  You don't
need to implement `setPoints()`, `getPost()`, or `draw()`.  For a digital chip,
you don't need to implement `stamp()`, `doStep()`, `getCurrentIntoNode()`, `getConnection()`,
`hasGroundConnection()`, or worry about currents.
* implement `stamp()` to stamp matrix values for linear elements.  This is called once when the circuit is
analyzed.  Call any of the various `sim.stamp*` methods.  Use the `nodes[]` array to get the node numbers to
pass to the stamp methods.  The posts are the first n nodes, followed by the internal nodes.  Again, make
sure all your nodes are connected to something when testing, or weird things may happen.
* implement `doStep()` to stamp additional matrix values for nonlinear elements.  This is called at least once
every timestep, possibly multiple times if it takes additional iterations for convergence.  Add an
implementation of `nonLinear()` which returns true if your element is nonlinear.  Use volts[] to access the
voltages of all the posts and internal nodes.
* implement `startIteration()` if you want to do some work before a simulation step starts, and/or
`stepFinished()` if you want to do work after it's done.
* test out your implementation.  If you have matrix problems, then you might want to go to
CirSim.java, and search for "uncomment this line to disable matrix simplification" to aid debugging.
* calculate current(s).  The simulator tells you the voltage of every post, but not the currents.  You
have to calculate those from the voltages.  The exception is if you have voltage sources; if you have more than
one, you need to implement `setCurrent()` to get the currents.  Otherwise, `current` is the current.
* update `draw()` to draw current.  For every separate current that flows through part of your element, you need
to calculate a curcount (the phase of the current animation) using `updateDotCount()`, and then draw it using
`drawDots()`.  For a simple two-terminal element where the current just moves from one terminal to the other,
you can call `doDots()`.  If your element has more than two elements, then there will be multiple currents
and multiple curcounts.
* implement `getEditInfo()` or `setEditInfo()` if your element has parameters that can be edited.  `getEditInfo()`
gets called multiple times to get a series of editable values.  It returns null when there are no more values.
Then `setEditInfo()` will get called if the user changes one of them.  If changing a value changes the
number of posts, then you need to call `allocNodes()` and `setPoints()`.
* implement the second constructor, the one that takes a StringTokenizer, so you can save circuits containing
your element.  Also implement dump(), starting by calling `super.dump()` and adding any other information you need.
Make a test circuit, then do File->Export as Text... and click Re-Import and make sure all your element's
state is remembered.  Boolean state can be stored as bits in `flags` which is saved and restored for you.  Check
parent classes to make sure you don't pick a bit that's in use.  
* make sure the voltage, current, and power are displayed correctly in the scope.  You can override `getVoltageDiff()`,
`getCurrent()`, and `getPower()` to fix this.
* implement `getInfo()` to display the mouse-over info.
* override `reset()` if there's extra work to be done there.
* remove (or fix) your implementation of getShortcut() if you copied another element that has one.  Override
it to return 0 (or something else) if you subclassed an element that has one.
* implement `getConnection()` to determine if your terminals are internally connected.  By default this just
returns true, meaning that all the terminals are connected to each other somehow.  This method is necessary in
case one of your terminals are not connected to something, to avoid a singular matrix.  For example,
a transistor has all terminals connected.  But a relay or a transformer do not; they have two separate
circuits.  So if someone creates a transistor with something connected to the primary, but nothing connected
to the secondary, then the voltage of the secondary will be undefined, causing a matrix error.  The simulator
calls this method to detect this and handle it.
* implement `hasGroundConnection()`.  This is similar to `getConnection()` but handles the case where one of your
terminals is connected to ground (or any other voltage reference).  For example, a digital chip output
returns true for `hasGroundConnection()`.
* implement `getCurrentIntoNode()`.  If you don't do this then wires connected to your element may have weird
currents.  Test it by doing something like the following: https://tinyurl.com/yyycodmj  Pretend the resistor
is your new element.  In this case, both terminals of your element are connected to wires which are connected to posts
that have shunt stub wires attached.  This forces the simulator to call your `getCurrentIntoNode()` to get the
wire currents.  If it looks correct then you are all set.  Otherwise you need to implement `getCurrentIntoNode()`
to return the wire flowing into node n out of your element.
* test of your element works in a subcircuit (if applicable).  Make a test circuit, make sure it works,
save it, and make sure you can reload it.  Note that `setPoints()` and `draw()` are never called for an element that's part
of a subcircuit, so if you have anything important in there, you may have problems.
* make sure your element works with a white background.  (Use `whiteColor` instead of `Color.white`)
* if you are modifying an existing element rather than creating a new one, then make sure your changes don't break
existing circuits.  Make sure you can still read the old dump format (if you changed it) and that everything
works the same.

# Program Loop

An incomplete description of the program loop and descriptions of major functions.

## `updateCircuit()`

This is the main loop. It runs after platform-specific initialization and loops continuously until program shutdown. It has two main sections:

- Run the circuit. Running the sim for a single timestep conditionally involves three main steps:
  1. Analyze the circuit: `analyzeCircuit()`
  2. Build the circuit matrices: `stampCircuit()`
  3. Run the simulation: `runCircuit()`
- Draw the circuit graphics
  - This is the simulation graphics state (i.e. the center screen). Menus and property interfaces are managed elsewhere (by GWT).

## `analyzeCircuit()`

Called when something in the circuit changes. This function does some initial setup for the overall simulation state, then searches the circuit for the presence of various invalid configurations and edge cases.

- Map groups of connected wire-like elements to the same node: `calculateWireClosure()`
- Sets the root ground node, important for the simulation: `setGroundNode()`
- Determines nodes that are not connected indirectly to ground: `findUnconnectedNodes()`.
  - All nodes must be connected to ground somehow, or else we will get a matrix error.
  - The unconnected nodes won't actually be connected here, but in the next step `stampCircuit()`.
- `validateCircuit()` searches for these invalid cases:
  - Inductors with no current path.
  - Current sources with no current path.
  - VCCS elements with no current path.
  - Voltage loops with no resistance.
  - Voltages that connect to ground with no resistance.
- `stampCircuit()` will always be called following `analyzeCircuit()`.

## `stampCircuit()`

Creates the MNA matrices using info gathered by `analyzeCircuit()` and fills them with data from each circuit element.

- `connectUnconnectedNodes()` connects isolated nodes by connecting them to ground with a large resistor.
- `simplifyMatrix()` is an important function which is run after the initial stamping of the matrix is complete. Since the number of steps required to solve a matrix is proportional to n^3, where n is the number of rows, it is worth a lot of effort to reduce the size of the matrix as much as possible.
- If the circuit is nonlinear, we can factor the circuit matrix one time inside `stampCircuit()` instead of needing to do it every frame (inside `runCircuit()`).

## `runCircuit()`

This function has two major loops: the *iteration loop* and the *subiteration loop*, the latter being a child of the former. The *iteration loop* can be thought of as executing a single full step of simulation. Each run of the *iteration loop* increments the circuit time by the timestep. The inner loop, called the *subiteration loop*, normally runs at least once per call to `runCircuit`. The *subiteration loop* tries to solve the circuit matrix (via `lu_solve()`). The number of times the *subiteration loop* runs inside the *iteration loop* depends on whether or not the circuit has converged.
