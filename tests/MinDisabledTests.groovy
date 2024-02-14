package joelwetzel.dimmer_minimums.tests

import me.biocomp.hubitat_ci.util.device_fixtures.DimmerFixtureFactory
import me.biocomp.hubitat_ci.util.integration.IntegrationAppSpecification
import me.biocomp.hubitat_ci.util.integration.TimeKeeper

import spock.lang.Specification

/**
* Tests of behavior when the dimmer is double-tapped up.
*/
class MinDisabledTests extends IntegrationAppSpecification {
    def dimmerFixture1 = DimmerFixtureFactory.create('d1')
    def dimmerFixture2 = DimmerFixtureFactory.create('d2')

    @Override
    def setup() {
        super.initializeEnvironment(appScriptFilename: "doubleTapToTheMax-app.groovy",
                                    userSettingValues: [dimmers: [dimmerFixture1, dimmerFixture2], enableMax: true, enableMin: false, maxValue: 100, minValue: 5, enableLogging: true])
        appScript.installed()
    }

    void "Min can be disabled"() {
        given:
        dimmerFixture1.initialize(appExecutor, [switch: "on", level: 50])

        when:
        dimmerFixture1.doubleTap(2)  // 2 is the number of down on the paddle

        then:
        dimmerFixture1.currentValue('switch') == "on"
        dimmerFixture1.currentValue('level') == 50
    }
}
