#!/bin/bash
set -o errexit -o nounset

SCRIPT_DIR="$(dirname "$(readlink -f "${BASH_SOURCE[0]}")")"
WORK_DIR="../workdir"
SDK_DIR=".."
GWT_VERSION="2.10.0"
GWT="$SDK_DIR/gwt-$GWT_VERSION"

compile() {
    ant build
}

setup() {
    # Install Java if no java compiler is present
    if ! which javac > /dev/null 2>&1; then
        echo "Installing packages may need your sudo password."
        set -x
        sudo apt install openjdk-8-jdk-headless
        set +x
    fi

    if ! [[ -d "$SDK_DIR" ]]; then
        mkdir -p "$SDK_DIR"
        cd "$SDK_DIR"
        wget "https://github.com/gwtproject/gwt/releases/download/$GWT_VERSION/gwt-$GWT_VERSION.zip"
        unzip "gwt-$GWT_VERSION.zip"
        rm "gwt-$GWT_VERSION.zip"
        set +x
    fi

    if ! [[ -e build.xml ]]; then
        chmod +x "$GWT/webAppCreator"
        "$GWT/webAppCreator" -out ../tempProject com.lushprojects.circuitjs1.circuitjs1
        cp ../tempProject/build.xml .
        rm -rf ../tempProject
    fi
}

codeserver() {
    mkdir -p war
    java -classpath "src:$GWT/gwt-codeserver.jar:$GWT/gwt-dev.jar:$GWT/gwt-user.jar" \
        com.google.gwt.dev.codeserver.CodeServer \
        -launcherDir war \
        com.lushprojects.circuitjs1.circuitjs1
}

webserver() {
    sourcedir="$(dirname "$(readlink -f "${BASH_SOURCE[0]}")")"
    #webroot="workdir/com.lushprojects.circuitjs1.circuitjs1/compile-1/war"
    webroot="war"

    #cp -r "$sourcedir/war/"*.html "$webroot"
    cd $webroot
    python -m http.server
}

start() {
    webserver >"webserver.log" 2>&1 &
    PID=$!
    codeserver | tee "codeserver.log"
    kill -INT "$PID"
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
