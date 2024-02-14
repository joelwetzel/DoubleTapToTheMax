/**
 *  Double Tap to the Max v1.3
 *
 *  Copyright 2021 Joel Wetzel
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */

definition(
    name: "Double Tap to the Max",
    namespace: "joelwetzel",
    author: "Joel Wetzel",
    description: "App to enable double-tap-up-for-max-brightness and double-tap-down-for-min-brightness across all your dimmers at once.",
    category: "Lighting",
	iconUrl: "",
    iconX2Url: "",
    iconX3Url: "")


def dimmers = [
		name:				"dimmers",
		type:				"capability.doubleTapableButton",
		title:				"Dimmers to control",
		description:		"Select the dimmers to control.",
		multiple:			true,
		required:			true
	]

def enableMax = [
		name:				"enableMax",
		type:				"bool",
		title:				"Double-tap up to Max Brightness?",
		defaultValue:		true,
		required:			true
	]

def enableMin = [
		name:				"enableMin",
		type:				"bool",
		title:				"Double-tap down to Min Brightness?",
		defaultValue:		true,
		required:			true
	]

def maxValue = [
		name:				"maxValue",
		type:				"number",
		title:				"Maximum brightness value",
		defaultValue:		100,
		required:			true
	]

def minValue = [
		name:				"minValue",
		type:				"number",
		title:				"Minimum brightness value",
		defaultValue:		5,
		required:			true
	]

def enableLogging = [
		name:				"enableLogging",
		type:				"bool",
		title:				"Enable Debug Logging?",
		defaultValue:		false,
		required:			true
	]


preferences {
	page(name: "mainPage", title: "Preferences", install: true, uninstall: true) {
		section(getFormat("title", "Double Tap to the Max")) {
            paragraph "If you don't want to wait and hold a dimmer paddle while it raises or lowers, this app lets you go to max or min brightness with double-taps.  It was created so that I wouldn't have to create individual Rule Machine rules for every dimmer in my house.  It takes advantage of how most dimmer drivers report double-tapping.  Generally, double-tap up is reported as button 1, and double-tap down is button 2."
		}
		section("") {
			input dimmers
            input enableMax
            input maxValue
            input enableMin
            input minValue
		}
		section () {
			input enableLogging
		}
        display()
	}
}


def installed() {
	log.info "Installed with settings: ${settings}"

	initialize()
}


def updated() {
	log.info "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}


def initialize() {
	subscribe(dimmers, "doubleTapped.1", doubleTapUpHandler)
    subscribe(dimmers, "doubleTapped.2", doubleTapDownHandler)
}


def doubleTapUpHandler(evt) {
    log "doubleTapUpHandler event:${evt.name},${evt.value},${evt.deviceId}"

    if (enableMax) {
        def triggeredDevice = dimmers.find { it.deviceId == evt.deviceId }
        triggeredDevice.setLevel(maxValue)
    }
}

def doubleTapDownHandler(evt) {
    log "doubleTapDownHandler event:${evt.name},${evt.value},${evt.deviceId}"

    if (enableMin) {
        def triggeredDevice = dimmers.find { it.deviceId == evt.deviceId }
        triggeredDevice.setLevel(minValue)
    }
}


def getFormat(type, myText=""){
	if(type == "header-green") return "<div style='color:#ffffff;font-weight: bold;background-color:#81BC00;border: 1px solid;box-shadow: 2px 3px #A9A9A9'>${myText}</div>"
    if(type == "line") return "\n<hr style='background-color:#1A77C9; height: 1px; border: 0;'></hr>"
	if(type == "title") return "<h2 style='color:#1A77C9;font-weight: bold'>${myText}</h2>"
}

def display(){
	section() {
		paragraph getFormat("line")
		paragraph "<div style='color:#1A77C9;text-align:center'>Double Tap to the Max - @joelwetzel<br><a href='https://github.com/joelwetzel/' target='_blank'>Click here for more Hubitat apps/drivers on my GitHub!</a></div>"
	}
}

def log(msg) {
	if (enableLogging) {
		log.debug msg
	}
}
