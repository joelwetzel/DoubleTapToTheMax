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
* Basic tests for dimmer-minimums.groovy
*/
class BasicTests extends Specification {
    // Creating a sandbox object for device script from file.
    private HubitatAppSandbox sandbox = new HubitatAppSandbox(new File('doubleTapToTheMax-app.groovy'))

    // Create mock log
    def log = Mock(Log)

    // Make AppExecutor return the mock log
    AppExecutor api = Mock { _ * getLog() >> log }

    void "Basic validation"() {
        given:

        expect:
        // Compile, construct script object, and validate definition() and preferences()
        sandbox.run()
    }

    void "installed() logs the settings"() {
        given:
        // Define a virtual dimmer device
        def dimmerDevice = new DeviceInputValueFactory([Switch, SwitchLevel, DoubleTapableButton])
            .makeInputObject('n', 't',  DefaultAndUserValues.empty(), false)

        // Run the app sandbox, passing the virtual dimmer device in.
        def script = sandbox.run(api: api,
            userSettingValues: [dimmers: [dimmerDevice], enableMax: true, enableMin: true, maxValue: 100, minValue: 5, enableLogging: true])

        when:
        // Run installed() method on app script.
        script.installed()

        then:
        // Expect that log.info() was called with this string
        1 * log.info('Installed with settings: [dimmers:[GeneratedDevice(input: n, type: t)], enableMax:true, enableMin:true, maxValue:100, minValue:5, enableLogging:true]')
    }

    void "initialize() subscribes to events"() {
        given:
        // Define a virtual dimmer device
        def dimmerDevice = new DeviceInputValueFactory([Switch, SwitchLevel, DoubleTapableButton])
            .makeInputObject('n', 't',  DefaultAndUserValues.empty(), false)

        // Run the app sandbox, passing the virtual dimmer device in.
        def script = sandbox.run(api: api,
            userSettingValues: [dimmers: [dimmerDevice], enableMax: true, enableMin: true, maxValue: 100, minValue: 5, enableLogging: true])

        when:
        // Run initialize() method on app script.
        script.initialize()

        then:
        // Expect that events are subscribe to
        1 * api.subscribe([dimmerDevice], 'doubleTapped.1', 'doubleTapUpHandler')
        1 * api.subscribe([dimmerDevice], 'doubleTapped.2', 'doubleTapDownHandler')
    }
}
