/*
Samsung WiFi Speaker - Soundbar
Beta Version 3

Copyright 2017 Dave Gutheinz

Licensed under the Apache License, Version 2.0 (the "License"); you 
may not use this  file except in compliance with the License. You may 
obtain a copy of the License at:

		http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
implied. See the License for the specific language governing 
permissions and limitations under the License.

This device handler interfaces to Samsung WiFi Soundbars.  Testing
was completed on the HW-MS650 Soundbar using commands derived from
internet data related to the 55001 port implementation of the 
Samsung Wireless Speakers.

Release Information:
	09-28-17	Beta Release of Manual Installation Version.
	10-03-17	Update Amazon and TuneIn Preset attainment.
				Add in Sensor and Actuator capability.
				Update Source selection to be a preference.
	10-05-17	Cleanup of code to improve performance.
    10-08-17	Update to support speakers in addition to
    			soundbars.
                a.  Change power to Active/Standby
                b.  Added Equalizer Preset Toggle
                c.  Removed Bass and Treble (not supported
                	speakers)
          		d.	Added scaling to volume
                	1.  Soundbar 0 .. 100
                    2.  Speaker 0 .. 30
	10-11-17	a.	Tuned each path for best performance.
    			b.	Reduced number of attributes
                c.	Moved Rear Speaker control to Preferences.
                d.	Added two presets (now 8)
*/

metadata {
	definition (name: "Samsung WiFi Speaker-Soundbar", namespace: "Beta.3", author: "Dave Gutheinz") {
		capability "Switch"
		capability "Refresh"
		capability "Music Player"
		capability "Sensor"
		capability "Actuator"
//	----- MUSIC PLAY -----
		command "inactive"
		command "toggleRepeat"
		command "toggleShuffle"
		attribute "repeat", "string"
		attribute "shuffle", "string"
//	----- SOUNDBAR CONTROL -----
		command "setLevel"
		command "setRear"
		command "setEqPreset"
        attribute "eqPreset", "string"
		attribute "rearLevel", "string"
		attribute "source", "string"
//	----- PRESETS -----
		command "preset1"
		command "preset2"
		command "preset3"
		command "preset4"
		command "preset5"
		command "preset6"
		command "preset7"
		command "preset8"
		attribute "preset1", "string"
		attribute "preset2", "string"
		attribute "preset3", "string"
		attribute "preset4", "string"
		attribute "preset5", "string"
		attribute "preset6", "string"
		attribute "preset7", "string"
		attribute "preset8", "string"
	}
	tiles(scale: 2) {
//	----- PLAYER CONTROL TILES -----
		multiAttributeTile(name: "main", type:"mediaPlayer", width:6, height:4, canChangeIcon: true) {
			tileAttribute("device.status", key: "PRIMARY_CONTROL") {
				attributeState("paused", label:"Paused",)
				attributeState("playing", label:"Playing")
			}
			tileAttribute("device.status", key: "MEDIA_STATUS") {
				attributeState("paused", label:"Paused", action:"play", nextState: "playing")
				attributeState("playing", label:"Playing", action:"pause", nextState: "paused")
			}
			tileAttribute("device.previous", key: "PREVIOUS_TRACK") {
				attributeState("default", action:"previousTrack")
			}
			tileAttribute("device.next", key: "NEXT_TRACK") {
				attributeState("default", action:"nextTrack")
			}
			tileAttribute ("device.level", key: "SLIDER_CONTROL") {
				attributeState("level", action:"setLevel")
			}
			tileAttribute ("device.mute", key: "MEDIA_MUTED") {
				attributeState("unmuted", action:"mute", nextState: "muted")
				attributeState("muted", action:"unmute", nextState: "unmuted")
			}
			tileAttribute("device.trackDescription", key: "MARQUEE") {
				attributeState("trackDesctiption", label:"${currentValue}")
			}
		}
		standardTile('shuffle', 'shuffle', decoration: 'flat', width: 1, height: 1) {
			state 'on', label: 'Shuffle', action: 'toggleShuffle', backgroundColor: '#00a0dc', nextState: 'off'
			state 'off', label: 'Shuffle', action: 'toggleShuffle', backgroundColor: '#ffffff', nextState: 'on'
			state 'inactive', label: "No\n\rShuffle", action: 'inactive', backgroundColor: '#ffffff'
 		}
		standardTile('repeat', 'repeat', decoration: 'flat', width: 1, height: 1) {
			state 'on', label: 'Repeat', action: 'toggleRepeat', backgroundColor: '#00a0dc', nextState: 'off'
			state 'off', label: 'Repeat', action: 'toggleRepeat', backgroundColor: '#ffffff', nextState: 'on'
			state 'inactive', label: 'No\n\rRepeat', action: 'inactive', backgroundColor: '#ffffff'
		}
//	----- SOUNDBAR CONTROL TILES -----
		standardTile('switch', 'device.switch', width: 1, height: 1, decoration: 'flat', canChangeIcon: true) {
			state 'on', label:'Soundbar Power', action:'off', backgroundColor:'#00a0dc', nextState:'off'
			state 'off', label:'Soundbar Power', action:'on', backgroundColor:'#ffffff', nextState:'on'
            state 'inactive', label: 'Speaker', action: 'inactive', backgroundColor: '#ffffff'
		}
		standardTile('updateDisplay', 'updateDisplay', width: 1, height: 1,  decoration: 'flat') {
			state ('default', label: 'Refresh Display', action: 'refresh')
		}
		standardTile('eqPreset', 'eqPreset', decoration: 'flat', width: 2, height: 1) {
        	state 'eqPreset', label: 'Equalizer:\n\r${currentValue}', action: 'setEqPreset'
		}
		valueTile('blank', 'default', height: 1, width: 2) {
			state 'default', label:''
		}
//	----- PRESET TILES -----
		standardTile('preset1', 'preset1', decoration: 'flat', width: 2, height: 1) {
			state 'preset1', label:'${currentValue}', action: 'preset1'
		}
		standardTile('preset2', 'preset2', decoration: 'flat', width: 2, height: 1) {
			state 'preset2', label:'${currentValue}', action: 'preset2'
		}
		standardTile('preset3', 'preset3', decoration: 'flat', width: 2, height: 1) {
			state 'preset3', label:'${currentValue}', action: 'preset3'
		}
		standardTile('preset4', 'preset4', decoration: 'flat', width: 2, height: 1) {
			state 'preset4', label:'${currentValue}', action: 'preset4'
		}
		standardTile('preset5', 'preset5', decoration: 'flat', width: 2, height: 1) {
			state 'preset5', label:'${currentValue}', action: 'preset5'
		}
		standardTile('preset6', 'preset6', decoration: 'flat', width: 2, height: 1) {
			state 'preset6', label:'${currentValue}', action: 'preset6'
		}
		standardTile('preset7', 'preset7', decoration: 'flat', width: 2, height: 1) {
			state 'preset7', label:'${currentValue}', action: 'preset7'
		}
		standardTile('preset8', 'preset8', decoration: 'flat', width: 2, height: 1) {
			state 'preset8', label:'${currentValue}', action: 'preset8'
		}
		valueTile('future', 'default', height: 1, width: 2) {
			state 'default', label:'future delete preset'
        }
        
		main "main"
		details(["main", 
        		 "switch", "updateDisplay", "eqPreset", "shuffle", "repeat",
//				 "blank", "blank", "blank",
				 'preset1', 'preset2', 'preset3',
                 'preset4', 'preset5', 'preset6',
                 'preset7', 'preset8', 'future'])
	}
}
def sources = [:]
sources<<["bt":"Bluetooth"]
sources<<["soundshare":"TV Sound Connect"]
sources<<["aux":"Auxiliary"]
sources<<["wifi":"WiFi"]
sources<<["optical":"HDMI(ARC) or Optical"]
sources<<["usb":"USB"]
sources<<["hdmi":"HDMI"]
sources<<["hdmi1":"HDMI #1"]
sources<<["hdmi2":"HDMI #2"]
def players = ["Amazon Station", "Amazon Playlist", "TuneIn", "iHeartRadio", "Pandora"]
def rearLevels = ["-6", "-5", "-4", "-3", "-2", "-1", "0", "1", "2", "3", "4", "5", "6"]
preferences {
	input name: "deviceIP", type: "text", title: "Device IP", required: true, displayDuringSetup: true
	input name: "newSource", type: "enum", title: "Speaker Source", options: sources, description: "Select source for this speaker", required: false
	input name: "rearLevel", type: "enum", title: "Rear Speaker Level", options: rearLevels, description: "Select Rear Speaker Vol Level", required: false
	input name: "preset1Player", type: "enum", title: "Preset 1 Player Name", options: players, description: "Select Player for Preset 2", required: false
	input name: "preset1Music", type: "text", title: "Preset 1 Station/Playlist", description: "Enter EXACT title", required: false
	input name: "preset1", type: "text", title: "Preset 1 Short Name", description: "Enter Lable name", required: false
	input name: "preset2Player", type: "enum", title: "Preset 2 Player Name", options: players, description: "Select Player for Preset 2", required: false
	input name: "preset2Music", type: "text", title: "Preset 2 Station/Playlist", description: "Enter EXACT title", required: false
	input name: "preset2", type: "text", title: "Preset 2 Short Name", description: "Enter Lable name", required: false
	input name: "preset3Player", type: "enum", title: "Preset 3 Player Name", options: players, description: "Select Player for Preset 3", required: false
	input name: "preset3Music", type: "text", title: "Preset 3 Station/Playlist", description: "Enter EXACT title", required: false
	input name: "preset3", type: "text", title: "Preset 3 Short Name", description: "Enter Lable name", required: false
	input name: "preset4Player", type: "enum", title: "Preset 4 Player Name", options: players, description: "Select Player for Preset 4", required: false
	input name: "preset4Music", type: "text", title: "Preset 4 Station/Playlist", description: "Enter EXACT title", required: false
	input name: "preset4", type: "text", title: "Preset 4 Short Name", description: "Enter Lable name", required: false
	input name: "preset5Player", type: "enum", title: "Preset 5 Player Name", options: players, description: "Select Player for Preset 5", required: false
	input name: "preset5Music", type: "text", title: "Preset 5 Station/Playlist", description: "Enter EXACT title", required: false
	input name: "preset5", type: "text", title: "Preset 5 Short Name", description: "Enter Lable name", required: false
	input name: "preset6Player", type: "enum", title: "Preset 6 Player Name", options: players, description: "Select Player for Preset 6", required: false
	input name: "preset6Music", type: "text", title: "Preset 6 Station/Playlist", description: "Enter EXACT title", required: false
	input name: "preset6", type: "text", title: "Preset 6 Short Name", description: "Enter Lable name", required: false
	input name: "preset7Player", type: "enum", title: "Preset 7 Player Name", options: players, description: "Select Player for Preset 7", required: false
	input name: "preset7Music", type: "text", title: "Preset 7 Station/Playlist", description: "Enter EXACT title", required: false
	input name: "preset7", type: "text", title: "Preset 7 Short Name", description: "Enter Lable name", required: false
	input name: "preset8Player", type: "enum", title: "Preset 8 Player Name", options: players, description: "Select Player for Preset 8", required: false
	input name: "preset8Music", type: "text", title: "Preset 8 Station/Playlist", description: "Enter EXACT title", required: false
	input name: "preset8", type: "text", title: "Preset 8 Short Name", description: "Enter Lable name", required: false
}

//	----- INITIALIZATION FUNCTIONS -----
def installed() {
//	Get the model and update the type-dependent volume scale
//	Will activate on the final software update.
	sendCmd("/UIC?cmd=%3Cname%3EGetSoftwareVersion%3C/name%3E")
    updateDataValue("currentEqPreset", "0")
    updated()
}
//	----- Update parameters changed in settings -----
/*
	Update runs twice.  I am calling a extra routine that 
    delays actual work for 2 seconds after unschedulihg.
    First time will be unscheduled before second calling,
    reducing running to only once.
*/
def updated() {
	delayUpdate()
}
def delayUpdate() {
	unschedule(update)
    runIn(2, update)
}

def update() {
	if (!getDataValue("currentEqPreset")) {
		updateDataValue("currentEqPreset", "0")
    }
/*    
	Get the model and update the type-dependent volume scale
	In final version, this will appear only in installed.  It
	will be commented out with instructions for temporary correction.
*/
	sendCmd("/UIC?cmd=%3Cname%3EGetSoftwareVersion%3C/name%3E")

//	Update the source after selection from list.
//	Future:  Toggle Source in app based on model series.
//	will take investigation of all wifi models for sources.
//	Known issue.  No way to update preference.
	if(device.currentValue("source") != newSource){
		setSource()
	}    
    
    setRear()
    
//	Sync preference presetN with attribute presetN
//	Future - delete the preference presetN using title for preset name.
	if(device.currentValue("preset1") != preset1) {
		sendEvent(name: "preset1", value: preset1)
	}
	if(device.currentValue("preset2") != preset2) {
		sendEvent(name: "preset2", value: preset2)
	}
	if(device.currentValue("preset3") != preset3) {
		sendEvent(name: "preset3", value: preset3)
	}
	if(device.currentValue("preset4") != preset4) {
		sendEvent(name: "preset4", value: preset4)
	}
	if(device.currentValue("preset5") != preset5) {
		sendEvent(name: "preset5", value: preset5)
	}
	if(device.currentValue("preset6") != preset6) {
		sendEvent(name: "preset6", value: preset6)
	}
	if(device.currentValue("preset7") != preset7) {
		sendEvent(name: "preset7", value: preset7)
	}
	if(device.currentValue("preset8") != preset8) {
		sendEvent(name: "preset8", value: preset8)
	}
}

//	SPEAKER/SOUNDBAR CONTROL FUNCTIONS
/*
	On-off assumption is that all soundbars have on/of
    function via the setPowerStatus command (basis:
    HW-MS650) and speakers do not.  Status is therefore
    on or off for soundbars.  For speakers, it is inactive.
    Inactive is set in the response to softwareversion.
*/
def on() {
	sendCmd("/UIC?cmd=%3Cname%3ESetPowerStatus%3C/name%3E" +
    		"%3Cp%20type=%22dec%22%20name=%22powerstatus%22%20val=%221%22/%3E")
    runIn(1, refresh)
}
def off() {
	sendCmd("/UIC?cmd=%3Cname%3ESetPowerStatus%3C/name%3E" +
    		"%3Cp%20type=%22dec%22%20name=%22powerstatus%22%20val=%220%22/%3E")
}
def setLevel(level) {
	def scale = getDataValue("volScale").toInteger()
	def deviceLevel = Math.round(scale*level/100).toInteger()
	sendCmd("/UIC?cmd=%3Cname%3ESetVolume%3C/name%3E" +
    		"%3Cp%20type=%22dec%22%20name=%22volume%22%20val=%22${deviceLevel}%22/%3E")
}
def mute() {
	sendCmd("/UIC?cmd=%3Cname%3ESetMute%3C/name%3E" +
    		"%3Cp%20type=%22str%22%20name=%22mute%22%20val=%22on%22/%3E")
}
def unmute() {
	sendCmd("/UIC?cmd=%3Cname%3ESetMute%3C/name%3E" +
    		"%3Cp%20type=%22str%22%20name=%22mute%22%20val=%22off%22/%3E")
}
def setSource() {
	sendCmd("/UIC?cmd=%3Cname%3ESetFunc%3C/name%3E" +
        	"%3Cp%20type=%22str%22%20name=%22function%22%20val=%22${newSource}%22/%3E")
	runIn(2, getSource)
}
def setRear() {
	sendCmd("/UIC?cmd=%3Cname%3ESetRearLevel%3C/name%3E" +
			"%3Cp%20type=%22dec%22%20name=%22rearlevel%22%20val=%22${rearLevel}%22/%3E" +
			"%3Cp%20type=%22str%22%20name=%22activate%22%20val=%22on%22/%3E" +
			"%3Cp%20type=%22dec%22%20name=%22connection%22%20val=%22on%22/%3E")
}
/*
	Equalizer preset is three methods.
    1)	get list to obtain total count of presets.
    2)	change the preset.
    3)	get the preset name and update state.
*/

def setEqPreset() {
	sendCmd("/UIC?cmd=%3Cname%3EGet7BandEQList%3C/name%3E")
}
def cmdEqPreset(totPresets) {
	def newEqPreset = ""
	def totalPresets = totPresets.toInteger() - 1
    def currentEqPreset = getDataValue("currentEqPreset").toInteger()
	if(currentEqPreset >= totalPresets) {
    	newEqPreset = 0
    } else {
    	newEqPreset = currentEqPreset + 1
	}
	sendCmd("/UIC?cmd=%3Cname%3ESet7bandEQMode%3C/name%3E" +
    		"%3Cp%20type=%22dec%22%20name=%22presetindex%22%20val=%22${newEqPreset}%22/%3E")
}
def getEqPresetName() {
	sendCmd("/UIC?cmd=%3Cname%3EGetCurrentEQMode%3C/name%3E")
}
//	----- SOUNDBAR STAUS FUNCTIONS -----
def getPwr() {
	if (getDataValue("hwType") == "HW-") {
	sendCmd("/UIC?cmd=%3Cname%3EGetPowerStatus%3C/name%3E")
    }
}
def getSource() {
	sendCmd("/UIC?cmd=%3Cname%3EGetFunc%3C/name%3E")
}
def getLevel() {
	sendCmd("/UIC?cmd=%3Cname%3EGetVolume%3C/name%3E")
}
def getMute() {
	sendCmd("/UIC?cmd=%3Cname%3EGetMute%3C/name%3E")
}
def getEQMode() {
	sendCmd("/UIC?cmd=%3Cname%3EGetEQMode%3C/name%3E")
}
//	----- PLAY/PAUSE -----
def play() {
	playPause("resume", "play")
    runIn(2, getPlayTime)
}
def pause() {
	playPause("pause", "pause")
	unschedule(setTrackDesciption)
}
def playPause(uicStatus, cpmStatus) {
   def submode = getDataValue("submode")
	switch(submode) {
		case "dlna":
			sendCmd("/UIC?cmd=%3Cname%3ESetPlaybackControl%3C/name%3E" +
            		"%3Cp%20type=%22str%22%20name=%22playbackcontrol%22%20val=%22${uicStatus}%22/%3E")
			break
		case "cp":
			sendCmd("/CPM?cmd=%3Cname%3ESetPlaybackControl%3C/name%3E" +
            		"%3Cp%20type=%22str%22%20name=%22playbackcontrol%22%20val=%22${cpmStatus}%22/%3E")
			break
		default:
			log.error "${device.label} Playback Control not valid for device or mode"
		 	return
	}
    getPlayStatus()
}
def getPlayStatus() {
	def submode = getDataValue("submode")
	switch(submode) {
		case "dlna":
			sendCmd("/UIC?cmd=%3Cname%3EGetPlayStatus%3C/name%3E")
			break
		case "cp":
			sendCmd("/CPM?cmd=%3Cname%3EGetPlayStatus%3C/name%3E")
			break
		default:
			return
	}
}
//	----- PREVIOUS/NEXT -----
def previousTrack() {
	trackChange("previous", "PreviousTrack")
}
def nextTrack() {
	trackChange("next", "SkipCurrentTrack")
}
def trackChange(uicTrackChg, cpmTrackChg) {
	def submode = getDataValue("submode")
	switch(submode) {
		case "dlna":
			sendCmd("/UIC?cmd=%3Cname%3ESetTrickMode%3C/name%3E" +
            		"%3Cp%20type=%22str%22%20name=%22trickmode%22%20val=%22${uicTrackChg}%22/%3E")
			break
		case "cp":
			sendCmd("/CPM?cmd=%3Cname%3ESet${cpmTrackChg}%3C/name%3E")
			break
		default:
			log.error "${device.label} Previous/Next not supported in current player"
			return
	}
    runIn(2, setTrackDescription)
}
//	Set the Marquee based on source and submode.
def setTrackDescription() {
	def submode = getDataValue("submode")
    def source = device.currentValue("source")
	unschedule(setTrackDesciption)
    if (source != "wifi") {
   	switch(source) {
        	case "bt":
            	sendEvent(name: "trackDescription", value: "Bluetooth")
            	break
            case "soundshare":
            	sendEvent(name: "trackDescription", value: "TV Sound Connect")
                break
            case "aux":
            	sendEvent(name: "trackDescription", value: "Auxiliary")
            	break
            case "optical":
            	sendEvent(name: "trackDescription", value: "HDMI(ARC) or Optical")
            	break
            case "usb":
            	sendEvent(name: "trackDescription", value: "USB")
            	break
            case "hdmi":
            	sendEvent(name: "trackDescription", value: "HDMI")
            	break
            case "hdmi1":
            	sendEvent(name: "trackDescription", value: "HDMI #1")
            	break
            case "hdmi2":
            	sendEvent(name: "trackDescription", value: "HDMI #2")
                break
            default:
            	sendEvent(name: "trackDescription", value: "Unknown Source")
            	break
        }
	} else {
		switch(submode) {
			case "dlna":
				sendCmd("/UIC?cmd=%3Cname%3EGetMusicInfo%3C/name%3E")
				break
			case "cp":
				sendCmd("/CPM?cmd=%3Cname%3EGetRadioInfo%3C/name%3E")
				break
			case "device":
				sendCmd("/UIC?cmd=%3Cname%3EGetAcmMode%3C/name%3E")
				break
			case "":
			default:
				sendEvent(name: "trackDescription", value: "Unknown WiFi")
                break
        }
		runIn(2, getPlayTime)
    }
}

def getPlayTime() {
	if(device.currentValue("status") == "paused") {
    	return
    } else {
		def currentCp = getDataValue("currentCp")
		if (currentCp == "TuneIn" || currentCp == "iHeartRadio") {
			log.info "${device.label} does not update TrackDescription for TuneIn nor iHeartRadio"
			return
		} else {
			def submode = getDataValue("submode")
			sendCmd("/UIC?cmd=%3Cname%3EGetCurrentPlayTime%3C/name%3E")
	    }
    }
}
def schedSetTrackDescription(playtime) {
	def trackLength = getDataValue("tracklength").toInteger()
	def nextUpdate = trackLength - playtime + 3
    if (nextUpdate < 0) {
    	log.error "${device.label} Next update value (${nextUpdate}) was negative!"
    } else {
		runIn(nextUpdate, setTrackDescription)
		log.debug "${device.label} Track Description will update in ${nextUpdate} seconds"
	}
}
def setLabels() {
	switch(getDataValue("submode")) {
		case "dlna":
			sendEvent(name: "shuffle", value: "off")
			sendEvent(name: "repeat", value: "off")
            break
		case "cp":
        	if (getDataValue("currentCp") == "Amazon") {
				sendEvent(name: "shuffle", value: "off")
				sendEvent(name: "repeat", value: "off")
            } else {
				sendEvent(name: "shuffle", value: "inactive")
				sendEvent(name: "repeat", value: "inactive")
            }
			break
		default:
			sendEvent(name: "shuffle", value: "inactive")
			sendEvent(name: "repeat", value: "inactive")
   }
}
//	----- SHUFFLE/REPEAT -----
def toggleShuffle() {
	def submode = getDataValue("submode")
    def shuffleCmd = ""
	 switch(submode) {
		case "dlna":
			if (device.currentValue("shuffle") == "off") {
            	shuffleCmd = "on"
			} else {
            	shuffleCmd = "off"
			}
			sendCmd("/UIC?cmd=%3Cname%3ESetShuffleMode%3C/name%3E" +
            		"%3Cp%20type=%22str%22%20name=%22shufflemode%22%20val=%22${shuffleCmd}%22/%3E")
			break
		case "cp":
			if (device.currentValue("shuffle") == "off") {
            	shuffleCmd = "1"
			} else {
            	shuffleCmd = "0"
			}
			sendCmd("/CPM?cmd=%3Cname%3ESetToggleShuffle%3C/name%3E" +
            		"%3Cp%20type=%22dec%22%20name=%22mode%22%20val=%22${shuffleCmd}%22/%3E")
			break
		default:
			log.error "${device.label} ShuffleMode not valid for device or mode"
		 	return
	}
}
def toggleRepeat() {
	def submode = getDataValue("submode")
    def repeatCmd = ""
	 switch(submode) {
		case "dlna":
			if (device.currentValue("repeat") == "off") {
            	repeatCmd = "one"
			} else {
            	repeatCmd = "off"
			}
			sendCmd("/UIC?cmd=%3Cname%3ESetRepeatMode%3C/name%3E" +
            		"%3Cp%20type=%22str%22%20name=%22repeatmode%22%20val=%22${repeatCmd}%22/%3E")
			break
		case "cp":
			if (device.currentValue("repeat") == "off") {
            	repeatCmd = "1"
			} else {
            	repeatCmd = "0"
			}
			sendCmd("/CPM?cmd=%3Cname%3ESetRepeatMode%3C/name%3E" +
            		"%3Cp%20type=%22dec%22%20name=%22mode%22%20val=%22${repeatCmd}%22/%3E")
			break
		default:
			log.error "${device.label} Repeat not valid for device or mode"
		 	return
	}
}
//	----- PRESET FUNCTIONS -----
def preset1() {
	playPreset(preset1Player, preset1Music)
}
def preset2() {
	playPreset(preset2Player, preset2Music)
}
def preset3() {
	playPreset(preset3Player, preset3Music)
}
def preset4() {
	playPreset(preset4Player, preset4Music)
}
def preset5() {
	playPreset(preset5Player, preset5Music)
}
def preset6() {
	playPreset(preset6Player, preset6Music)
}
def preset7() {
	playPreset(preset7Player, preset7Music)
}
def preset8() {
	playPreset(preset8Player, preset8Music)
}
def playPreset(player, music) {
//	log.debug "${device.label} starting preset with player = ${player} and music = ${music}"
	updateDataValue("currentPlayer", "${player}")
    updateDataValue("currentMusic", "${music}")
	if (player == "Amazon Playlist") {
		sendCmd("/CPM?cmd=%3Cname%3ESetSelectAmazonCp%3C/name%3E")
	} else if (player == "Amazon Station") {
		sendCmd("/CPM?cmd=%3Cname%3ESetCpService%3C/name%3E" +
				"%3Cp%20type%3D%22dec%22%20name%3D%22cpservice_id%22%20val%3D%2224%22/%3E")
	} else if (player == "TuneIn") {
		sendCmd("/CPM?cmd=%3Cname%3ESetSelectRadio%3C/name%3E")
	} else if (player == "iHeartRadio") {		
		sendCmd("/CPM?cmd=%3Cname%3ESetCpService%3C/name%3E" +
				"%3Cp%20type%3D%22dec%22%20name%3D%22cpservice_id%22%20val%3D%225%22/%3E")
	} else if (player == "Pandora") {
		sendCmd("/CPM?cmd=%3Cname%3ESetCpService%3C/name%3E" +
				"%3Cp%20type%3D%22dec%22%20name%3D%22cpservice_id%22%20val%3D%220%22/%3E")
	} else {
		log.error "${device.label} failed to set CP in playPreset."
	}
}
def selectSubmenu() {
	def player = getDataValue("currentPlayer")
    def contentId = ""
	if (player == "Amazon Playlist") {
		contentId = "1"
	} else if (player == "Amazon Station") {
    	contentId = "2"
	} else if (player == "iHeartRadio") {
    	contentId = "1"
	} else if (player == "TuneIn") {
		sendCmd("/CPM?cmd=%3Cname%3EGetPresetList%3C/name%3E" +
				"%3Cp%20type%3D%22dec%22%20name%3D%22startindex%22%20val%3D%220%22/%3E" +
				"%3Cp%20type%3D%22dec%22%20name%3D%22listcount%22%20val%3D%2210%22/%3E")
        return
	} else if (player == "Pandora") {
		sendCmd("/CPM?cmd=%3Cname%3EGetCurrentRadioList%3C/name%3E" +
				"%3Cp%20type%3D%22dec%22%20name%3D%22startindex%22%20val%3D%220%22/%3E" +
				"%3Cp%20type%3D%22dec%22%20name%3D%22listcount%22%20val%3D%2299%22/%3E")
        return
	} else {
		log.error "${device.label} failed to set Submenu in selectSubmenu."
    	return
    }
	sendCmd("/CPM?cmd=%3Cname%3ESetSelectCpSubmenu%3C/name%3E" +
			"%3Cp%20type%3D%22dec%22%20name%3D%22contentid%22%20val%3D%22${contentId}%22/%3E" +
			"%3Cp%20type%3D%22dec%22%20name%3D%22startindex%22%20val%3D%220%22/%3E" +
			"%3Cp%20type%3D%22dec%22%20name%3D%22listcount%22%20val%3D%2230%22/%3E")
}
def selectRadioList(contentId) {
	sendCmd("/CPM?cmd=%3Cname%3EGetSelectRadioList%3C/name%3E" +
    		"%3Cp%20type=%22dec%22%20name=%22contentid%22%20val=%22${contentId}%22/%3E" +
            "%3Cp%20type=%22dec%22%20name=%22startindex%22%20val=%220%22/%3E" +
            "%3Cp%20type=%22dec%22%20name=%22listcount%22%20val=%2230%22/%3E")
}
def SetPlaySelect(contentId) {
//	log.debug "${device.label} At SetPlaySelect with contentId = ${contentId}"
	sendCmd("/CPM?cmd=%3Cname%3ESetPlaySelect%3C/name%3E" +
			"%3Cp%20type=%22dec%22%20name=%22selectitemid%22%20val=%22${contentId}%22/%3E")
	log.info "${device.label} Playing preset ${getDataValue("currentMusic")} on ${getDataValue("currentPlayer")}"
    runIn(5, getSource)
}
def SetPlayPreset(contentId) {
//	log.debug "${device.label} At SetPlayPreset with contentId = ${contentId}"
	sendCmd("/CPM?cmd=%3Cname%3ESetPlayPreset%3C/name%3E" +
			"%3Cp%20type%3D%22dec%22%20name%3D%22presetindex%22%20val%3D%22${contentId}%22/%3E" +
			"%3Cp%20type%3D%22dec%22%20name%3D%22presettype%22%20val%3D%221%22/%3E")
	log.info "${device.label} Playing preset ${getDataValue("currentMusic")} on ${getDataValue("currentPlayer")}"
	runIn(5, getSource)
}
//	----- TURN SHUFFLE/REPEAT CAPABILITY ON AND OFF -----
def inactive() {
	log.error "${device.label} The specific tile is not active due to mode of player."
}
/*
	Refresh is used to update the display when music is started
	the multiroom app.  It can also be used if an out-of-sync
	condition is encountered due to some timing issue.
*/
def refresh() {
	getSource()
    getLevel()
	runIn(1, getPlayStatus)
    runIn(1, getMute)
    runIn(2, setLabels)
    runIn(2, getEqPresetName)
    runIn(3, getPwr)
    runIn(3, setTrackDescription)
}
/*
	If an unplanned Method is returned from the device, it could
	mask the desired Method.  The purpose of this (invalic)
	command is to cause any buffered method to be transmitted
	for parsing.
*/
def getMore() {
//log.debug "EXECUTED getMore"
	sendCmd("/UIC?cmd=%3Cname%3EGetMore%3C/name%3E")
}
//	----- SEND COMMAND TO SOUNDBAR -----
private sendCmd(command){
//	log.debug command
	def cmdStr = new physicalgraph.device.HubAction([
		method: "GET",
		path: command,
		headers: [
			HOST: "${deviceIP}:55001"
		]],
		null,
		[callback: parseResponse]
	)
	sendHubCommand(cmdStr)
}
//	----- PARSE RESPONSE DATA BASED ON METHOD -----
/*
	Parsing will be of the returned methods from the device.
	The device sends these responses asynchronously; therefore,
	some timing issues and missed responses may occur.
*/
void parseResponse(resp) {
	def respMethod = (new XmlSlurper().parseText(resp.body)).method
	def respData = (new XmlSlurper().parseText(resp.body)).response
	switch(respMethod) {
//	----- SOUNDBAR STATUS METHODS -----
		case "PowerStatus":
        	if (respData.powerStatus == "1") {
            	sendEvent(name: "switch", value: "on")
                sendEvent(name: "mute", value: "unmuted")
            } else {
            	sendEvent(name: "switch", value: "off")
            }
			break
		case "CurrentFunc":
			sendEvent(name: "source", value: respData.function)
            updateDataValue("submode", "${respData.submode}")
			log.info "${device.label} Source = ${device.currentValue("source")}, Submode = ${getDataValue("submode")}"
            getPlayStatus()
            runIn(1, setTrackDescription)
			break
		case "VolumeLevel":
        	def scale = getDataValue("volScale").toInteger()
        	def level = respData.volume.toInteger()
        	level = Math.round(100*level/scale).toInteger()
			sendEvent(name: "level", value: level)
//			log.debug "${device.label} Volume set to ${device.currentValue("level")}"
			break
		case "MuteStatus":
			if (respData.mute == "on") {
				sendEvent(name: "mute", value: "muted")
			} else {
				sendEvent(name: "mute", value: "unmuted")
			}
//			log.debug "${device.label} Device Mute is set to ${device.currentValue("mute")}"
	   		break
		case "7BandEQList":
			cmdEqPreset(respData.listcount.toString())
			break
        case "EQMode":
        case "EQDrc":
			getEqPresetName()
        	break
        case "7bandEQMode":
        case "CurrentEQMode":
			sendEvent(name: "eqPreset", value: respData.presetname)
			updateDataValue("currentEqPreset", "${respData.presetindex}")
//			log.info "${device.label} Equalizer Preset now ${device.currentValue("eqPreset")}"
            break
		case "RearLevel":
	   	sendEvent(name: "rearLevel", value: respData.level)
//			log.debug "${device.label} Rear Level is at ${device.currentValue("rearLevel")}."
			break
//	----- MEDIA CONTROL STATUS METHODS -----
		case "PlayStatus":
		case "PlaybackStatus":
			if (respData.playstatus == "play") {
				sendEvent(name: "status", value: "playing")
			} else if (respData.playstatus == "stop" || "pause" || "paused") {
				sendEvent(name: "status", value: "paused")
		   }
			break
		case "RepeatMode":
			def submode = getDataValue("submode")
			if (submode == "dlna") {
				if (respData.repeat == "one") {
					sendEvent(name: "repeat", value: "on")
				} else {
					sendEvent(name: "repeat", value: "off")
				}
		   } else if (submode == "cp") {
		   	if (respData.repeatmode == "1") {
					sendEvent(name: "repeat", value: "on")
				} else {
					sendEvent(name: "repeat", value: "off")
				}
			}
			break
		case "ShuffleMode":
			sendEvent(name: "shuffle", value: respData.shuffle)
			break
		case "ToggleShuffle":
			if (respData.shufflemode == "1") {
				sendEvent(name: "shuffle", value: "on")
			} else {
				sendEvent(name: "shuffle", value: "off")
			}
			break
//	----- METHODS TO UPDATE TRACK DESCRIPTION -----
		case "MusicInfo":
			sendEvent(name: "trackDescription", value: "${respData.title}\n${respData.artist}")
			updateDataValue("currentCp", "")
//			log.debug "${device.label} trackDescription set to ${device.currentValue("trackDescription")}"
            break
		case "RadioInfo":
        	if (respData.cpname == "TuneIn" || respData.cpname == "iHeartRadio") {
				sendEvent(name: "trackDescription", value: respData.title)
            } else {
				sendEvent(name: "trackDescription", value: "${respData.artist}\n${respData.title}")
            }
			updateDataValue("currentCp", "${respData.cpname}")
            updateDataValue("currentMusic", "${respData.title}")
//			log.debug "${device.label} trackDescription set to ${device.currentValue("trackDescription")}"
			if (respData.tracklength != "" && respData.tracklength.toInteger() > 1) {
				updateDataValue("tracklength", "${respData.tracklength}")
			}
			break
		case "AcmMode":
			sendEvent(name: "trackDescription", value: respData.audiosourcename)
//			log.debug "${device.label} trackDescription set to ${device.currentValue("trackDescription")}"
			break
		case "MusicPlayTime":
			if (getDataValue("submode") == "dlna") {
				updateDataValue("tracklength", "${respData.timelength}")
			}
            if (respData.playtime != ""){
	            schedSetTrackDescription(respData.playtime.toInteger())
            }
			break
//	----- TUNE PRESET METHODS -----
		case "CpChanged":
		case "RadioSelected":
		case "AmazonCpSelected":
//			log.debug "${device.label} currentCp changed to ${respData.cpname}"
			updateDataValue("currentCp", "${respData.cpname}")
            setLabels()
            selectSubmenu()
			break
		case "RadioList":
			def contentId = ""
			def music = getDataValue("currentMusic")
//			log.debug "${device.label} At parse RadioList with music = ${music}"
			def menuItems = respData.menulist.menuitem
           if (getDataValue("currentPlayer") == "Amazon Playlist" && respData.category == music) {
            	SetPlaySelect("0")
            } else {
				menuItems.each {
					if (contentId == "") {
						if (it.title == music) {
							contentId = it.contentid
						}
					}
                }
                if (getDataValue("currentPlayer") == "Amazon Playlist") {
					selectRadioList(contentId)
                } else {
					SetPlaySelect(contentId)
                }
			}
			break
		case "PresetList":
			def contentId = ""
			def presets = respData.presetlist.preset
			def music = getDataValue("currentMusic")
//			log.debug "${device.label} At parse PresetList with music = ${music}"
			presets.each {
				if (contentId == "") {
					if (it.title == music) {
						contentId = it.contentid
						SetPlayPreset(contentId)
					}
				}
		   }
			if (contentId == "") {
				log.error "${device.label} The TuneIn preset title is not valid"
			}
			break
        case "SoftwareVersion":
        	def model = respData.version.toString()
            updateDataValue("model", "${model}")
            def hwType = model.substring(0,3)
            updateDataValue("hwType", "${hwType}")
			if (hwType == "WAM") {
				updateDataValue("volScale", "30")
                sendEvent(name: "switch", value: "inactive")
		    } else {
				updateDataValue("volScale", "100")
                sendEvent(name: "switch", value: "on")
		    }
			log.info "${device.label} Updated device Model and Volume Scale"
			break
        case "StartPlaybackEvent":
        case "MediaBufferStartEvent":
				sendEvent(name: "status", value: "playing")
                getMore()
        	break
		case "StopPlaybackEvent":
		case "EndPlaybackEvent":
		case "MediaBufferEndEvent":
		case "PausePlaybackEvent":
//			sendEvent(name: "status", value: "pause")
//        	break
        case "MusicList":
        case "RequestDeviceInfo":
        case "MultiQueueList":
        case "RadioPlayList":
        case "ManualSpeakerUpgrade":
        	getMore()
	        log.info "${device.label} Intentional ignore of parse method ${respMethod}"
            break
 		default:
            getMore()
	        log.info "${device.label} New parse method:  ${respMethod} with data ${respData}"
        	break
	}
}