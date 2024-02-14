package joelwetzel.dimmer_minimums.tests

import me.biocomp.hubitat_ci.util.device_fixtures.DimmerFixtureFactory
import me.biocomp.hubitat_ci.util.integration.IntegrationAppSpecification
import me.biocomp.hubitat_ci.util.integration.TimeKeeper

import spock.lang.Specification

/**
* Tests of behavior when the dimmer is double-tapped up.
*/
class MaxDisabledTests extends IntegrationAppSpecification {
    def dimmerFixture1 = DimmerFixtureFactory.create('d1')
    def dimmerFixture2 = DimmerFixtureFactory.create('d2')

    @Override
    def setup() {
        super.initializeEnvironment(appScriptFilename: "doubleTapToTheMax-app.groovy",
                                    userSettingValues: [dimmers: [dimmerFixture1, dimmerFixture2], enableMax: false, enableMin: true, maxValue: 100, minValue: 5, enableLogging: true])
        appScript.installed()
    }

    void "Max can be disabled"() {
        given:
        dimmerFixture1.initialize(appExecutor, [switch: "off", level: 10])

        when:
        dimmerFixture1.doubleTap(1)  // 1 is the number of up on the paddle

        then:
        dimmerFixture1.currentValue('switch') == "off"
        dimmerFixture1.currentValue('level') == 10
    }
}
