/**
Copyright 2018 Dave Gutheinz

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
in compliance with the License. You may obtain a copy of the License at:

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
for the specific language governing permissions and limitations under the License.

Speaker Custom Message
Author: Dave Gutheinz
From the SmartThings "Speaker Notify with Sound" to change method of custom text delivery to the
device from Audio Notifications to playTextAndResume or playTextAndRestore.

Capabilities:
a.	Same standard sounds as the original Speaker Notify with sound.
b.	Instead of generating the sound then sending as an Audio Notification,
	this app sends a "playTextAndRestore" or playTextAndResume command
	to the speaker device handler.  Allows for alternate speechToText
	engines.

History:
	2018-03-24.  Finished initial creation.
 */
definition(
	name: "TTS Speaker Notify with Sound",
	namespace: "davegut",
	author: "Dave Gutheinz",
	description: "Play a sound or custom message through your Speaker when the mode changes or other events occur.  Modified to use playText commands for custom text.",
	category: "SmartThings Labs",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/MiscHacking/mindcontrol.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/MiscHacking/mindcontrol@2x.png"
)

preferences {
	page(name: "mainPage", title: "Play a message on your SAMSUNG AUDIO SPEAKER when something happens", install: true, uninstall: true)
	page(name: "timeIntervalInput", title: "Only during a certain time") {
		section {
			input "starting", "time", title: "Starting", required: false
			input "ending", "time", title: "Ending", required: false
		}
	}
}

def mainPage() {
	dynamicPage(name: "mainPage") {
		def anythingSet = anythingSet()
		if (anythingSet) {
			section("Play message when"){
				ifSet "motion", "capability.motionSensor", title: "Motion Here", required: false, multiple: true
				ifSet "contact", "capability.contactSensor", title: "Contact Opens", required: false, multiple: true
				ifSet "contactClosed", "capability.contactSensor", title: "Contact Closes", required: false, multiple: true
				ifSet "acceleration", "capability.accelerationSensor", title: "Acceleration Detected", required: false, multiple: true
				ifSet "mySwitch", "capability.switch", title: "Switch Turned On", required: false, multiple: true
				ifSet "mySwitchOff", "capability.switch", title: "Switch Turned Off", required: false, multiple: true
				ifSet "arrivalPresence", "capability.presenceSensor", title: "Arrival Of", required: false, multiple: true
				ifSet "departurePresence", "capability.presenceSensor", title: "Departure Of", required: false, multiple: true
				ifSet "smoke", "capability.smokeDetector", title: "Smoke Detected", required: false, multiple: true
				ifSet "water", "capability.waterSensor", title: "Water Sensor Wet", required: false, multiple: true
				ifSet "button1", "capability.button", title: "Button Press", required:false, multiple:true //remove from production
				ifSet "triggerModes", "mode", title: "System Changes Mode", required: false, multiple: true
				ifSet "timeOfDay", "time", title: "At a Scheduled Time", required: false
			}
		}
		def hideable = anythingSet || app.installationState == "COMPLETE"
		def sectionTitle = anythingSet ? "Select additional triggers" : "Play message when..."

		section(sectionTitle, hideable: hideable, hidden: true){
			ifUnset "motion", "capability.motionSensor", title: "Motion Here", required: false, multiple: true
			ifUnset "contact", "capability.contactSensor", title: "Contact Opens", required: false, multiple: true
			ifUnset "contactClosed", "capability.contactSensor", title: "Contact Closes", required: false, multiple: true
			ifUnset "acceleration", "capability.accelerationSensor", title: "Acceleration Detected", required: false, multiple: true
			ifUnset "mySwitch", "capability.switch", title: "Switch Turned On", required: false, multiple: true
			ifUnset "mySwitchOff", "capability.switch", title: "Switch Turned Off", required: false, multiple: true
			ifUnset "arrivalPresence", "capability.presenceSensor", title: "Arrival Of", required: false, multiple: true
			ifUnset "departurePresence", "capability.presenceSensor", title: "Departure Of", required: false, multiple: true
			ifUnset "smoke", "capability.smokeDetector", title: "Smoke Detected", required: false, multiple: true
			ifUnset "water", "capability.waterSensor", title: "Water Sensor Wet", required: false, multiple: true
			ifUnset "button1", "capability.button", title: "Button Press", required:false, multiple:true //remove from production
			ifUnset "triggerModes", "mode", title: "System Changes Mode", description: "Select mode(s)", required: false, multiple: true
			ifUnset "timeOfDay", "time", title: "At a Scheduled Time", required: false
		}
		section{
			input "actionType", "enum", title: "Action?", required: true, defaultValue: "Bell 1", options: [
				"Custom Message",
				"Bell 1",
				"Bell 2",
				"Dogs Barking",
				"Fire Alarm",
				"Piano",
				"Lightsaber"]
			input "message","text",title:"Play this message", required:false, multiple: false
		}
		section {
			input "speaker", "capability.audioNotification", title: "On this Speaker player", required: true
			input "ttsVoice", "enum", title: "TTS Voice", required: true, defaultValue: "Salli", options: [
				"Geraint":"Geraint, English(Welch) Male","Gwyneth":"Gwyneth, Welsh Female",
                "Mads":"Mads, Danish Male","Naja":"Naja, Danish Female","Hans":"Hans, German Male",
                "Marlene":"Marlene, German Female","Vicki":"Vicki, German Female",
                "Nicole":"Nicole, English(AU) Female","Russell":"Russell, English(AU) Male",
                "Amy":"Amy, English(GB) Female","Brian":"Brian, English(GB) Male",
                "Emma":"Emma, English(GB) Female","Aditi":"Aditi, English(IN) Female",
                "Raveena":"Raveena, English(IN) Female","Ivy":"Ivy, English(US) Female",
                "Joanna":"Joanna, English(US) Female","Joey":"Joey, English(US) Male",
                "Justin":"Justin, English(US) Male","Kendra":"Kendra, English(US) Female",
                "Kimberly":"Kimberly, English(US) Female","Matthew":"Matthew, English(US) Male",
                "Salli":"Salli, English(US) Female","Conchita":"Conchita, Sapnish(Castilian) Female",
                "Enrique":"Enrique, Sapnish(Castilian) Male","Miguel":"Miguel, Spanish(LatinAmer) Male",
                "Penelope":"Penelope, Spanish(LatinAmer) Female","Chantal":"Chantal, French(Canadian) Female",
                "Celine":"Celine, French Female","Mathieu":"Mathieu, French Male","Dora":"Dora, Icelandic Female",
                "Karl":"Karl, Icelandic Male","Carla":"Carla, italian Female","Giorgio":"Giorgio, italian Male",
                "Mizuki":"Mizuki, Japanese Female","Takumi":"Takumi, Japanese Male",
                "Seoyeon":"Seoyeon, Korean Female","Liv":"Liv, Norwegian Female","Lotte":"Lotte, Dutch Female",
                "Ruben":"Ruben, Dutch Male","Ewa":"Ewa, Polish Female","Jacek":"Jacek, Polish Male",
                "Jan":"Jan, Polish Male","Maja":"Maja, Polish Female","Ricardo":"Ricardo, Portuguese(Brazil) Male",
                "Vitoria":"Vitoria, Portuguese(Brazil) Female","Cristiano":"Cristiano, Portuguese(EU) Male",
                "Ines":"Ines, Portuguese(EU) Female","Carmen":"Carmen, Romanian Female",
                "Maxim":"Maxim, Russian Male","Tatyana":"Tatyana, Russian Female",
                "Astrid":"Astrid, Sweedish Female","Filiz":"Filiz, Turkish Female"]
		}
		section("More options", hideable: true, hidden: true) {
			input "resumePlaying", "bool", title: "Resume currently playing music after notification", required: false, defaultValue: true
			input "volume", "number", title: "Temporarily change volume", description: "0-100%", required: false
			input "frequency", "decimal", title: "Minimum time between actions (defaults to every event)", description: "Minutes", required: false
			href "timeIntervalInput", title: "Only during a certain time", description: timeLabel ?: "Tap to set", state: timeLabel ? "complete" : "incomplete"
			input "days", "enum", title: "Only on certain days of the week", multiple: true, required: false,
				options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
			if (settings.modes) {
            	input "modes", "mode", title: "Only when mode is", multiple: true, required: false
            }
			input "oncePerDay", "bool", title: "Only once per day", required: false, defaultValue: false
		}
		section([mobileOnly:true]) {
			label title: "Assign a name", required: false
			mode title: "Set for specific mode(s)", required: false
		}
	}
}

private anythingSet() {
	for (name in ["motion","contact","contactClosed","acceleration","mySwitch","mySwitchOff","arrivalPresence","departurePresence","smoke","water","button1","timeOfDay","triggerModes","timeOfDay"]) {
		if (settings[name]) {
			return true
		}
	}
	return false
}

private ifUnset(Map options, String name, String capability) {
	if (!settings[name]) {
		input(options, name, capability)
	}
}

private ifSet(Map options, String name, String capability) {
	if (settings[name]) {
		input(options, name, capability)
	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	subscribeToEvents()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	unschedule()
	subscribeToEvents()
}

def subscribeToEvents() {
	subscribe(app, appTouchHandler)
	subscribe(contact, "contact.open", eventHandler)
	subscribe(contactClosed, "contact.closed", eventHandler)
	subscribe(acceleration, "acceleration.active", eventHandler)
	subscribe(motion, "motion.active", eventHandler)
	subscribe(mySwitch, "switch.on", eventHandler)
	subscribe(mySwitchOff, "switch.off", eventHandler)
	subscribe(arrivalPresence, "presence.present", eventHandler)
	subscribe(departurePresence, "presence.not present", eventHandler)
	subscribe(smoke, "smoke.detected", eventHandler)
	subscribe(smoke, "smoke.tested", eventHandler)
	subscribe(smoke, "carbonMonoxide.detected", eventHandler)
	subscribe(water, "water.wet", eventHandler)
	subscribe(button1, "button.pushed", eventHandler)

	if (triggerModes) {
		subscribe(location, modeChangeHandler)
	}

	if (timeOfDay) {
		schedule(timeOfDay, scheduledTimeHandler)
	}

	loadText()
}

def eventHandler(evt) {
	log.trace "eventHandler($evt?.name: $evt?.value)"
	if (allOk) {
		log.trace "allOk"
		def lastTime = state[frequencyKey(evt)]
		if (oncePerDayOk(lastTime)) {
			if (frequency) {
				if (lastTime == null || now() - lastTime >= frequency * 60000) {
					takeAction(evt)
				}
				else {
					log.debug "Not taking action because $frequency minutes have not elapsed since last action"
				}
			}
			else {
				takeAction(evt)
			}
		}
		else {
			log.debug "Not taking action because it was already taken today"
		}
	}
}

def modeChangeHandler(evt) {
	log.trace "modeChangeHandler $evt.name: $evt.value ($triggerModes)"
	if (evt.value in triggerModes) {
		eventHandler(evt)
	}
}

def scheduledTimeHandler() {
	eventHandler(null)
}

def appTouchHandler(evt) {
	takeAction(evt)
}

private takeAction(evt) {
    if (state.soundType == "track") {
		log.info "Sending TRACK:  ${state.sound.uri} // ${state.sound.duration} // ${volume}"
		if (resumePlaying){
			speaker.playTrackAndResume(state.sound.uri, state.sound.duration, volume)
 		} else {
			speaker.playTrackAndRestore(state.sound.uri, state.sound.duration, volume)
        }
	} else {
		log.debug "Sending Text to speaker: ${state.sound} // ${volume} // ${ttsVoice}"
		if (resumePlaying){
//        	speaker.playTextAndResume(state.sound, volume)
        	speaker.playTextAsVoiceAndResume(state.sound, volume, ttsVoice)
 		} else {
//        	speaker.playTextAndRestore(state.sound, volume)
        	speaker.playTextAsVoiceAndRestore(state.sound, volume, ttsVoice)
        }
    }
	if (frequency || oncePerDay) {
		state[frequencyKey(evt)] = now()
	}
}

private frequencyKey(evt) {
	"lastActionTimeStamp"
}

private dayString(Date date) {
	def df = new java.text.SimpleDateFormat("yyyy-MM-dd")
	if (location.timeZone) {
		df.setTimeZone(location.timeZone)
	}
	else {
		df.setTimeZone(TimeZone.getTimeZone("America/New_York"))
	}
	df.format(date)
}

private oncePerDayOk(Long lastTime) {
	def result = true
	if (oncePerDay) {
		result = lastTime ? dayString(new Date()) != dayString(new Date(lastTime)) : true
		log.trace "oncePerDayOk = $result"
	}
	result
}

private getAllOk() {
	modeOk && daysOk && timeOk
}

private getModeOk() {
	def result = !modes || modes.contains(location.mode)
	log.trace "modeOk = $result"
	result
}

private getDaysOk() {
	def result = true
	if (days) {
		def df = new java.text.SimpleDateFormat("EEEE")
		if (location.timeZone) {
			df.setTimeZone(location.timeZone)
		}
		else {
			df.setTimeZone(TimeZone.getTimeZone("America/New_York"))
		}
		def day = df.format(new Date())
		result = days.contains(day)
	}
	log.trace "daysOk = $result"
	result
}

private getTimeOk() {
	def result = true
	if (starting && ending) {
		def currTime = now()
		def start = timeToday(starting, location?.timeZone).time
		def stop = timeToday(ending, location?.timeZone).time
		result = start < stop ? currTime >= start && currTime <= stop : currTime <= stop || currTime >= start
	}
	log.trace "timeOk = $result"
	result
}

private hhmm(time, fmt = "h:mm a") {
	def t = timeToday(time, location.timeZone)
	def f = new java.text.SimpleDateFormat(fmt)
	f.setTimeZone(location.timeZone ?: timeZone(time))
	f.format(t)
}

private getTimeLabel() {
	(starting && ending) ? hhmm(starting) + "-" + hhmm(ending, "h:mm a z") : ""
}

private loadText() {
	state.soundType = "track"
	switch ( actionType) {
		case "Bell 1":
			state.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/bell1.mp3", duration: "10"]
			break;
		case "Bell 2":
			state.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/bell2.mp3", duration: "10"]
			break;
		case "Dogs Barking":
			state.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/dogs.mp3", duration: "10"]
			break;
		case "Fire Alarm":
			state.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/alarm.mp3", duration: "17"]
			break;
		case "Piano":
			state.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/piano2.mp3", duration: "10"]
			break;
		case "Lightsaber":
			state.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/lightsaber.mp3", duration: "10"]
			break;
		case "Custom Message":
			state.soundType = "text"
            if (!message) {
                message = "No custom text entered into application"
             }
            	state.sound = message
			break;
		default:
			state.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/bell1.mp3", duration: "10"]
			break;
	}
}