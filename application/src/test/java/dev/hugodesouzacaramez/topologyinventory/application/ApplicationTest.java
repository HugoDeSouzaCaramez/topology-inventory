package dev.hugodesouzacaramez.topologyinventory.application;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/dev/hugodesouzacaramez/topologyinventory/application", // Diretório raiz das features
        glue = "dev.hugodesouzacaramez.topologyinventory.application", // Pacote com os steps
        plugin = {"pretty", "html:target/cucumber-reports.html"} // Relatórios opcionais
)
public class ApplicationTest {
}
