package joelwetzel.dimmer_minimums.tests

import me.biocomp.hubitat_ci.util.device_fixtures.DimmerFixtureFactory
import me.biocomp.hubitat_ci.util.integration.IntegrationAppSpecification
import me.biocomp.hubitat_ci.util.integration.TimeKeeper

import spock.lang.Specification

/**
* Basic tests for doubleTapToTheMax-app.groovy
*/
class BasicTests extends IntegrationAppSpecification {
    def dimmerFixture = DimmerFixtureFactory.create('n')

    @Override
    def setup() {
        super.initializeEnvironment(appScriptFilename: "doubleTapToTheMax-app.groovy",
                                    userSettingValues: [dimmers: [dimmerFixture], enableMax: true, enableMin: true, maxValue: 100, minValue: 5, enableLogging: true])
    }

    void "installed() logs the settings"() {
        when:
        appScript.installed()

        then:
        1 * log.info('Installed with settings: [dimmers:[GeneratedDevice(input: n, type: t)], enableMax:true, enableMin:true, maxValue:100, minValue:5, enableLogging:true]')
    }

    void "initialize() subscribes to events"() {
        when:
        appScript.initialize()

        then:
        1 * appExecutor.subscribe([dimmerFixture], 'doubleTapped.1', 'doubleTapUpHandler')
        1 * appExecutor.subscribe([dimmerFixture], 'doubleTapped.2', 'doubleTapDownHandler')
    }
}
