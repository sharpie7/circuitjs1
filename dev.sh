#!/bin/bash
set -o errexit -o nounset # bash script safety

# For GWT download URLs see https://www.gwtproject.org/versions.html
GWT_VERSION="2.8.2"
#GWT_URL="https://github.com/gwtproject/gwt/releases/download/2.10.0/gwt-2.10.0.zip"
#GWT_URL="https://storage.googleapis.com/gwt-releases/gwt-2.9.0.zip"
GWT_URL="https://goo.gl/pZZPXS" # 2.8.2
#GWT_URL="https://goo.gl/TysXZl" # 2.8.1 (does not run)

SCRIPT_DIR="$(dirname "$(readlink -f "${BASH_SOURCE[0]}")")"
SDK_DIR="$SCRIPT_DIR/.."
GWT_DIR="$SDK_DIR/gwt-$GWT_VERSION"

compile() {
    ant build
}

package() {
    compile
    (
        cd "$SCRIPT_DIR/war"
        tar czf "$SCRIPT_DIR/circuitjs1.tar.gz" .
    )
}

setup() {
    # Install Java if no java compiler is present
    if ! which javac > /dev/null 2>&1 ||  ! which ant > /dev/null 2>&1; then
        echo "Installing packages may need your sudo password."
        set -x
        sudo apt-get update
        sudo apt-get install -y openjdk-8-jdk-headless ant
        set +x
    fi

    if ! [[ -d "$GWT_DIR" ]]; then
        mkdir -p "$SDK_DIR"
        (
            cd "$SDK_DIR"
            wget "$GWT_URL" -O "gwt-$GWT_VERSION.zip"
            unzip "gwt-$GWT_VERSION.zip"
            rm "gwt-$GWT_VERSION.zip"
            set +x
        )
    fi

    if [[ -e build.xml ]]; then
        mv build.xml build.xml.backup
    fi
    chmod +x "$GWT_DIR/webAppCreator"
    "$GWT_DIR/webAppCreator" -out ../tempProject com.lushprojects.circuitjs1.circuitjs1
    cp ../tempProject/build.xml ./
    sed -i 's/source="1.7"/source="1.8"/g' build.xml
    sed -i 's/target="1.7"/target="1.8"/g' build.xml
    rm -rf ../tempProject
}

codeserver() {
    mkdir -p war
    java -classpath "src:$GWT_DIR/gwt-codeserver.jar:$GWT_DIR/gwt-dev.jar:$GWT_DIR/gwt-user.jar" \
        com.google.gwt.dev.codeserver.CodeServer \
        -launcherDir war \
        com.lushprojects.circuitjs1.circuitjs1
}

webserver() {
    webroot="$SCRIPT_DIR/war"

    (
        cd $webroot
        python -m http.server
    )
}

start() {
    echo "Starting web server http://127.0.0.1:8000"
    trap "pkill -f \"python -m http.server\"" EXIT
    webserver >"webserver.log" 2>&1 &
    sleep 0.5
    codeserver | tee "codeserver.log"
}


for func in $(compgen -A function); do
    if [[ $func == "$1" ]]; then
        shift
        $func "$@"
        exit $?
    fi
done

echo "Unknown command '$1'. Try one of the following:"
compgen -A function
exit 1
