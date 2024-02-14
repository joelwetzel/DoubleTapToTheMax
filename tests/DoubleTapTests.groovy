package joelwetzel.dimmer_minimums.tests

import me.biocomp.hubitat_ci.util.device_fixtures.DimmerFixtureFactory
import me.biocomp.hubitat_ci.util.integration.IntegrationAppSpecification
import me.biocomp.hubitat_ci.util.integration.TimeKeeper

import spock.lang.Specification

/**
* Tests of behavior when the dimmer is double-tapped up.
*/
class DoubleTapTests extends IntegrationAppSpecification {
    def dimmerFixture1 = DimmerFixtureFactory.create('d1')
    def dimmerFixture2 = DimmerFixtureFactory.create('d2')

    @Override
    def setup() {
        super.initializeEnvironment(appScriptFilename: "doubleTapToTheMax-app.groovy",
                                    userSettingValues: [dimmers: [dimmerFixture1, dimmerFixture2], enableMax: true, enableMin: true, maxValue: 100, minValue: 5, enableLogging: true])
        appScript.installed()
    }

    void "Double-tap up maxes the dimmer"() {
        given:
        dimmerFixture1.initialize(appExecutor, [switch: "off", level: 10])

        when:
        dimmerFixture1.doubleTap(1)  // 1 is the number of up on the paddle

        then:
        dimmerFixture1.currentValue('switch') == "on"
        dimmerFixture1.currentValue('level') == 100
    }

    void "Double-tap down mins the dimmer"() {
        given:
        dimmerFixture1.initialize(appExecutor, [switch: "on", level: 50])

        when:
        dimmerFixture1.doubleTap(2)  // 2 is the number of down on the paddle

        then:
        dimmerFixture1.currentValue('switch') == "on"
        dimmerFixture1.currentValue('level') == 5
    }

    void "Double-tap down mins the dimmer, even if off"() {
        given:
        dimmerFixture1.initialize(appExecutor, [switch: "off", level: 50])

        when:
        dimmerFixture1.doubleTap(2)  // 2 is the number of down on the paddle

        then:
        dimmerFixture1.currentValue('switch') == "on"
        dimmerFixture1.currentValue('level') == 5
    }


    void "doubleTapUpHandler() adjusts correct dimmer from among multiple devices"() {
        given:
        dimmerFixture1.initialize(appExecutor, [switch: "off", level: 50])
        dimmerFixture2.initialize(appExecutor, [switch: "off", level: 50])

        when:
        dimmerFixture2.doubleTap(1)  // 1 is the number of up on the paddle

        then:
        dimmerFixture2.currentValue('switch') == "on"
        dimmerFixture2.currentValue('level') == 100
        dimmerFixture1.currentValue('switch') == "off"
        dimmerFixture1.currentValue('level') == 50
    }
}
