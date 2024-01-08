package joelwetzel.dimmer_minimums.tests

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

    // Make AppExecutor return the mock log
    AppExecutor api = Mock { _ * getLog() >> log }

    private def constructMockDimmerDevice(String name, Map state) {
        def dimmerDevice = new DeviceInputValueFactory([Switch, SwitchLevel, DoubleTapableButton])
            .makeInputObject(name, 't',  DefaultAndUserValues.empty(), false)
        dimmerDevice.getMetaClass().state = state
        dimmerDevice.getMetaClass().on = { state.switch = "on" }
        dimmerDevice.getMetaClass().off = { state.switch = "off" }
        dimmerDevice.getMetaClass().setLevel = {
            int level ->
                state.level = level
                state.switch = level > 0 ? "on" : "off"
        }

        return dimmerDevice
    }

    void "Double-tap up maxes the dimmer"() {
        given:
        // Define a virtual dimmer device
        def dimmerDevice = constructMockDimmerDevice('n', [switch: "off", level: 10])

        // Run the app sandbox, passing the virtual dimmer device in.
        def script = sandbox.run(api: api,
            userSettingValues: [dimmers: [dimmerDevice], enableMax: true, enableMin: true, maxValue: 100, minValue: 5, enableLogging: true],
        )

        when:
        script.doubleTapUpHandler([deviceId: dimmerDevice.deviceId, value: true])

        then:
        dimmerDevice.state.switch == "on"
        dimmerDevice.state.level == 100
    }

    void "Max can be disabled"() {
        given:
        // Define a virtual dimmer device
        def dimmerDevice = constructMockDimmerDevice('n', [switch: "off", level: 10])

        // Run the app sandbox, passing the virtual dimmer device in.
        def script = sandbox.run(api: api,
            userSettingValues: [dimmers: [dimmerDevice], enableMax: false, enableMin: true, maxValue: 100, minValue: 5, enableLogging: true],
        )

        when:
        script.doubleTapUpHandler([deviceId: dimmerDevice.deviceId, value: true])

        then:
        dimmerDevice.state.switch == "off"
        dimmerDevice.state.level == 10
    }

    void "Double-tap down mins the dimmer"() {
        given:
        // Define a virtual dimmer device
        def dimmerDevice = constructMockDimmerDevice('n', [switch: "on", level: 50])

        // Run the app sandbox, passing the virtual dimmer device in.
        def script = sandbox.run(api: api,
            userSettingValues: [dimmers: [dimmerDevice], enableMax: true, enableMin: true, maxValue: 100, minValue: 5, enableLogging: true],
        )

        when:
        script.doubleTapDownHandler([deviceId: dimmerDevice.deviceId, value: true])

        then:
        dimmerDevice.state.switch == "on"
        dimmerDevice.state.level == 5
    }

    void "Min can be disabled"() {
        given:
        // Define a virtual dimmer device
        def dimmerDevice = constructMockDimmerDevice('n', [switch: "on", level: 50])

        // Run the app sandbox, passing the virtual dimmer device in.
        def script = sandbox.run(api: api,
            userSettingValues: [dimmers: [dimmerDevice], enableMax: true, enableMin: false, maxValue: 100, minValue: 5, enableLogging: true],
        )

        when:
        script.doubleTapDownHandler([deviceId: dimmerDevice.deviceId, value: true])

        then:
        dimmerDevice.state.switch == "on"
        dimmerDevice.state.level == 50
    }

    void "Double-tap down mins the dimmer, even if off"() {
        given:
        // Define a virtual dimmer device
        def dimmerDevice = constructMockDimmerDevice('n', [switch: "off", level: 100])

        // Run the app sandbox, passing the virtual dimmer device in.
        def script = sandbox.run(api: api,
            userSettingValues: [dimmers: [dimmerDevice], enableMax: true, enableMin: true, maxValue: 100, minValue: 5, enableLogging: true],
        )

        when:
        script.doubleTapDownHandler([deviceId: dimmerDevice.deviceId, value: true])

        then:
        dimmerDevice.state.switch == "on"
        dimmerDevice.state.level == 5
    }


    void "doubleTapUpHandler() adjusts correct dimmer from among multiple devices"() {
        given:
        // Define two virtual dimmer devices
        def dimmerDevice1 = constructMockDimmerDevice('n1', [switch: "off", level: 50])
        def dimmerDevice2 = constructMockDimmerDevice('n2', [switch: "off", level: 50])

        // Run the app sandbox, passing the virtual dimmer devices in.
        def script = sandbox.run(api: api,
            userSettingValues: [dimmers: [dimmerDevice1, dimmerDevice2], enableMax: true, enableMin: true, maxValue: 100, minValue: 5, enableLogging: true],
        )

        when:
        script.doubleTapUpHandler([deviceId: dimmerDevice2.deviceId, value: true])

        then:
        dimmerDevice2.state.switch == "on"
        dimmerDevice2.state.level == 100
        dimmerDevice1.state.switch == "off"
        dimmerDevice1.state.level == 50
    }
}
