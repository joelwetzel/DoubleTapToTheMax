package joelwetzel.dimmer_minimums.tests

import me.biocomp.hubitat_ci.util.device_fixtures.DimmerFixtureFactory
import me.biocomp.hubitat_ci.util.IntegrationAppExecutor

import me.biocomp.hubitat_ci.api.app_api.AppExecutor
import me.biocomp.hubitat_ci.api.common_api.Log
import me.biocomp.hubitat_ci.app.HubitatAppSandbox
import me.biocomp.hubitat_ci.api.common_api.DeviceWrapper
import me.biocomp.hubitat_ci.app.preferences.DeviceInputValueFactory
import me.biocomp.hubitat_ci.capabilities.GeneratedCapability
import me.biocomp.hubitat_ci.capabilities.Switch
import me.biocomp.hubitat_ci.capabilities.SwitchLevel
import me.biocomp.hubitat_ci.capabilities.DoubleTapableButton
import me.biocomp.hubitat_ci.util.NullableOptional
import me.biocomp.hubitat_ci.validation.DefaultAndUserValues
import me.biocomp.hubitat_ci.validation.Flags
import me.biocomp.hubitat_ci.validation.GeneratedDeviceInputBase

import spock.lang.Specification

/**
* Tests of behavior when the dimmer is double-tapped up.
*/
class DoubleTapTests extends Specification {
    // Creating a sandbox object for device script from file.
    private HubitatAppSandbox sandbox = new HubitatAppSandbox(new File('doubleTapToTheMax-app.groovy'))

    // Create mock log
    def log = Mock(Log)

    def appExecutor = Spy(IntegrationAppExecutor) {
        _*getLog() >> log
    }

    void "Double-tap up maxes the dimmer"() {
        given:
        def dimmerFixture = DimmerFixtureFactory.create('n')

        // Run the app sandbox, passing the virtual dimmer device in.
        def appScript = sandbox.run(api: appExecutor,
            userSettingValues: [dimmers: [dimmerFixture], enableMax: true, enableMin: true, maxValue: 100, minValue: 5, enableLogging: true],
        )
        appExecutor.setSubscribingScript(appScript)
        dimmerFixture.initialize(appExecutor, [switch: "off", level: 10])

        when:
        appScript.installed()
        dimmerFixture.doubleTap(1)  // 1 is the number of up on the paddle

        then:
        dimmerFixture.state.switch == "on"
        dimmerFixture.state.level == 100
    }

    void "Max can be disabled"() {
        given:
        def dimmerFixture = DimmerFixtureFactory.create('n')

        // Run the app sandbox, passing the virtual dimmer device in.
        def appScript = sandbox.run(api: appExecutor,
            userSettingValues: [dimmers: [dimmerFixture], enableMax: false, enableMin: true, maxValue: 100, minValue: 5, enableLogging: true],
        )
        appExecutor.setSubscribingScript(appScript)
        dimmerFixture.initialize(appExecutor, [switch: "off", level: 10])

        when:
        appScript.installed()
        dimmerFixture.doubleTap(1)  // 1 is the number of up on the paddle

        then:
        dimmerFixture.state.switch == "off"
        dimmerFixture.state.level == 10
    }

    void "Double-tap down mins the dimmer"() {
        given:
        def dimmerFixture = DimmerFixtureFactory.create('n')

        // Run the app sandbox, passing the virtual dimmer device in.
        def appScript = sandbox.run(api: appExecutor,
            userSettingValues: [dimmers: [dimmerFixture], enableMax: true, enableMin: true, maxValue: 100, minValue: 5, enableLogging: true],
        )
        appExecutor.setSubscribingScript(appScript)
        dimmerFixture.initialize(appExecutor, [switch: "on", level: 50])

        when:
        appScript.installed()
        dimmerFixture.doubleTap(2)  // 2 is the number of down on the paddle

        then:
        dimmerFixture.state.switch == "on"
        dimmerFixture.state.level == 5
    }

    void "Min can be disabled"() {
        given:
        def dimmerFixture = DimmerFixtureFactory.create('n')

        // Run the app sandbox, passing the virtual dimmer device in.
        def appScript = sandbox.run(api: appExecutor,
            userSettingValues: [dimmers: [dimmerFixture], enableMax: true, enableMin: false, maxValue: 100, minValue: 5, enableLogging: true],
        )
        appExecutor.setSubscribingScript(appScript)
        dimmerFixture.initialize(appExecutor, [switch: "on", level: 50])

        when:
        appScript.installed()
        dimmerFixture.doubleTap(2)  // 2 is the number of down on the paddle

        then:
        dimmerFixture.state.switch == "on"
        dimmerFixture.state.level == 50
    }

    void "Double-tap down mins the dimmer, even if off"() {
        given:
        def dimmerFixture = DimmerFixtureFactory.create('n')

        // Run the app sandbox, passing the virtual dimmer device in.
        def appScript = sandbox.run(api: appExecutor,
            userSettingValues: [dimmers: [dimmerFixture], enableMax: true, enableMin: true, maxValue: 100, minValue: 5, enableLogging: true],
        )
        appExecutor.setSubscribingScript(appScript)
        dimmerFixture.initialize(appExecutor, [switch: "off", level: 50])

        when:
        appScript.installed()
        dimmerFixture.doubleTap(2)  // 2 is the number of down on the paddle

        then:
        dimmerFixture.state.switch == "on"
        dimmerFixture.state.level == 5
    }


    void "doubleTapUpHandler() adjusts correct dimmer from among multiple devices"() {
        given:
        // Define two virtual dimmer devices
        def dimmerFixture1 = DimmerFixtureFactory.create('n1')
        def dimmerFixture2 = DimmerFixtureFactory.create('n2')

        // Run the app sandbox, passing the virtual dimmer devices in.
        def appScript = sandbox.run(api: appExecutor,
            userSettingValues: [dimmers: [dimmerFixture1, dimmerFixture2], enableMax: true, enableMin: true, maxValue: 100, minValue: 5, enableLogging: true],
        )
        appExecutor.setSubscribingScript(appScript)

        dimmerFixture1.initialize(appExecutor, [switch: "off", level: 50])
        dimmerFixture2.initialize(appExecutor, [switch: "off", level: 50])

        when:
        appScript.installed()
        dimmerFixture2.doubleTap(1)  // 1 is the number of up on the paddle

        then:
        dimmerFixture2.state.switch == "on"
        dimmerFixture2.state.level == 100
        dimmerFixture1.state.switch == "off"
        dimmerFixture1.state.level == 50
    }
}
