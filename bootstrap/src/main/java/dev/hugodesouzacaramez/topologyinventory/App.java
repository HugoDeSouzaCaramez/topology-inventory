package dev.hugodesouzacaramez.topologyinventory.bootstrap;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class App {
    public static void main(String ... args) {
        Quarkus.run(args);
    }
}