/*
Samsung WiFi Soundbar
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

Functions supported:
a.  Soundbar control: on/off, volume, mute, source, bass level,
	treble level, rear level.
b.	Music control:  play/pause, next, previous, shuffle, repeat.
c.	Preset Channels.  User enters Channel, Station (or Track), and
	a short name.  After setting up the station/playlist in specific
	locations through the MultiRoom app.  Can generate up to six 
	preset channels from:
	1.  Amazon Prime - Amazon Stations
	2.	Amazon Prime - My Music
	3.	TuneIn - Preset stations
	4.	iHeartRadio - Favorites
	5.	Pandora - top menu.
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
*/

metadata {
	definition (name: "Samsung WiFi Soundbar", namespace: "djg", author: "Dave Gutheinz") {
		capability "Switch"
		capability "Refresh"
		capability "Music Player"
		capability "Sensor"
		capability "Actuator"
//	----- MUSIC PLAY -----
		command "inactive"
		command "toggleRepeat"
		command "toggleShuffle"
		attribute "currentCp", "string"
		attribute "currentPlayer", "string"
		attribute "currentMusic", "string"
		attribute "playtime", "number"
		attribute "repeat", "string"
		attribute "shuffle", "string"
		attribute "tracklength", "number"
//	----- SOUNDBAR CONTROL -----
		command "setLevel"
		command "setRear"
		command "setEqPreset"
        attribute "hwType", "string"
        attribute "volScale", "number"
        attribute "eqPreset", "string"
        attribute "eqPresetCount", "number"
        attribute "currentEqPreset", "number"
		attribute "rearLevel", "string"
		attribute "source", "string"
		attribute "submode", "string"
//	----- PRESETS -----
		command "preset1", ["string"]
		command "preset2"
		command "preset3"
		command "preset4"
		command "preset5"
		command "preset6"
		attribute "preset1", "string"
		attribute "preset2", "string"
		attribute "preset3", "string"
		attribute "preset4", "string"
		attribute "preset5", "string"
		attribute "preset6", "string"
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
			state 'inactive', action: 'inactive', backgroundColor: '#ffffff'
 		}
		standardTile('repeat', 'repeat', decoration: 'flat', width: 1, height: 1) {
			state 'on', label: 'Repeat', action: 'toggleRepeat', backgroundColor: '#00a0dc', nextState: 'off'
			state 'off', label: 'Repeat', action: 'toggleRepeat', backgroundColor: '#ffffff', nextState: 'on'
			state 'inactive', action: 'inactive', backgroundColor: '#ffffff'
		}
//	----- SOUNDBAR CONTROL TILES -----
		standardTile('switch', 'device.switch', width: 1, height: 1, decoration: 'flat', canChangeIcon: true) {
			state 'on', label:'Active\n\rOn', action:'off', backgroundColor:'#00a0dc', nextState:'off'
			state 'off', label:'Standby\n\rOff', action:'on', backgroundColor:'#ffffff', nextState:'on'
		}
		standardTile('blank', 'default', width: 2, height: 1) {
			state ('default', label: '')
		}		 
		valueTile('source', 'source', width: 1, height: 1) {
			state ('default', label: '${currentValue}')
		}		 
		standardTile('updateDisplay', 'updateDisplay', width: 2, height: 1,  decoration: 'flat') {
			state ('default', label: 'Refresh Display', action: 'refresh')
		}
		standardTile('eqPreset', 'eqPreset', decoration: 'flat', width: 2, height: 1) {
        	state 'eqPreset', label: 'Equalizer:\n\r${currentValue}', action: 'setEqPreset'
		}
		valueTile('blank', 'default', height: 1, width: 2) {
			state 'default', label:''
		}
		controlTile('rear', 'device.rearLevel', 'slider', height: 1, width: 1, range: '(-6..6)') {
			state 'rearLevel', action:'setRear'
		}
		valueTile('rearLabel', 'default', height: 1, width: 1) {
			state 'default', label:'Rear Level'
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
		main "main"

		details(["main", "switch", "source", "updateDisplay", "shuffle", "repeat",
					"eqPreset", "blank", "rear", "rearLabel", 
					'preset1', 'preset2', 'preset3', 'preset4', 'preset5', 'preset6'])
	}
}
def sources = [:]
sources<<["bt":"Bluetooth"]
sources<<["soundshare":"TV Sound Connect"]
sources<<["aux":"Aux"]
sources<<["wifi":"WiFi"]
sources<<["optical":"HDMI(ARC) or Optical"]
sources<<["usb":"USB"]
sources<<["hdmi":"HDMI"]
sources<<["hdmi1":"HDMI #1"]
sources<<["hdmi2":"HDMI #2"]
def players = ["Amazon Station", "Amazon Playlist", "TuneIn", "iHeartRadio", "Pandora"]
preferences {
	input name: "deviceIP", type: "text", title: "Device IP", required: true, displayDuringSetup: true
	input name: "newSource", type: "enum", title: "Speaker Source", options: sources, description: "Select source for this speaker", required: false
	input name: "preset1Player", type: "enum", title: "Preset 1 Player Name", options: players, description: "Select Player for Preset 1", required: false
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
}

//	----- INITIALIZATION FUNCTIONS -----
def installed() {
	sendEvent(name: "source", value: "wifi")
    getHardwareType()
}
//	----- Update parameters changed in settings -----
def updated() {
	getHardwareType()
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
	if(device.currentValue("source") != newSource){
log.debug "AT UPDATED Source with newsource = ${newSource} and current = ${device.currentValue("source")}"
		sendCmd("/UIC?cmd=%3Cname%3ESetFunc%3C/name%3E" +
        		"%3Cp%20type=%22str%22%20name=%22function%22%20val=%22${newSource}%22/%3E")
	}
}
def getHardwareType() {
	sendCmd("/UIC?cmd=%3Cname%3EGetSoftwareVersion%3C/name%3E")
    runIn(2, setVolumeScale)
}
def setVolumeScale() {
	if (device.currentValue("hwType") == "WAM") {
    	sendEvent(name: "volScale", value: 30)
    } else {
    	sendEvent(name: "volScale", value: 100)
    }
}

//	----- SOUNDBAR CONTROL FUNCTIONS -----

def on() {
	play()
	sendCmd("/UIC?cmd=%3Cname%3ESetPowerStatus%3C/name%3E" +
    		"%3Cp%20type=%22dec%22%20name=%22powerstatus%22%20val=%221%22/%3E")
	getSource()
	sendEvent(name: "switch", value: "on")
}
def off() {
    pause()
	sendCmd("/UIC?cmd=%3Cname%3ESetPowerStatus%3C/name%3E" +
    		"%3Cp%20type=%22dec%22%20name=%22powerstatus%22%20val=%220%22/%3E")
	sendEvent(name: "switch", value: "off")
}
def setLevel(level) {
	def scale = device.currentValue("volScale")
	def intLevel = Math.round(scale*level/100).toInteger()
	sendCmd("/UIC?cmd=%3Cname%3ESetVolume%3C/name%3E" +
    		"%3Cp%20type=%22dec%22%20name=%22volume%22%20val=%22${intLevel}%22/%3E")
}
def mute() {
	sendCmd("/UIC?cmd=%3Cname%3ESetMute%3C/name%3E" +
    		"%3Cp%20type=%22str%22%20name=%22mute%22%20val=%22on%22/%3E")
}
def unmute() {
	sendCmd("/UIC?cmd=%3Cname%3ESetMute%3C/name%3E" +
    		"%3Cp%20type=%22str%22%20name=%22mute%22%20val=%22off%22/%3E")
}
def setEqPreset() {
	sendCmd("/UIC?cmd=%3Cname%3EGet7BandEQList%3C/name%3E")
    runIn(2, cmdEqPreset)
}
def cmdEqPreset() {
	def newEqPreset = ""
	if(device.currentValue("eqPresetCount")-1 <= device.currentValue("currentEqPreset")) {
    	newEqPreset = 0
    } else {
    	newEqPreset = device.currentValue("currentEqPreset") + 1
	}
	sendCmd("/UIC?cmd=%3Cname%3ESet7bandEQMode%3C/name%3E" +
    		"%3Cp%20type=%22dec%22%20name=%22presetindex%22%20val=%22${newEqPreset}%22/%3E")
	runIn(2, getEqPresetName)
}
def getEqPresetName() {
	sendCmd("/UIC?cmd=%3Cname%3EGetCurrentEQMode%3C/name%3E")
}
def setRear(rearLevel) {
	sendCmd("/UIC?cmd=%3Cname%3ESetRearLevel%3C/name%3E" +
			"%3Cp%20type=%22dec%22%20name=%22rearlevel%22%20val=%22${rearLevel}%22/%3E" +
			"%3Cp%20type=%22str%22%20name=%22activate%22%20val=%22on%22/%3E" +
			"%3Cp%20type=%22dec%22%20name=%22connection%22%20val=%22on%22/%3E")
}

//	----- SOUNDBAR STAUS FUNCTIONS -----
def getPwr() {
	sendCmd("/UIC?cmd=%3Cname%3EGetPowerStatus%3C/name%3E")
}
def getSource() {
	sendCmd("/UIC?cmd=%3Cname%3EGetFunc%3C/name%3E")
    refresh()
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
def getRear() {
	sendCmd("/UIC?cmd=%3Cname%3EGetRearLevel%3C/name%3E")
}

//	----- PLAY/PAUSE -----
def play() {
	playPause("resume", "play")
    getPlayTime()
}
def pause() {
	playPause("pause", "pause")
	unschedule(setTrackDesciption)
}
def playPause(uicStatus, cpmStatus) {
   def submode = device.currentValue("submode")
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
	def submode = device.currentValue("submode")
	switch(submode) {
		case "dlna":
			sendCmd("/UIC?cmd=%3Cname%3EGetPlayStatus%3C/name%3E")
			break
		case "cp":
			sendCmd("/CPM?cmd=%3Cname%3EGetPlayStatus%3C/name%3E")
			break
		default:
			log.error "${device.label} Playback Control not valid for device or mode"
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
	def submode = device.currentValue("submode")
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
    runIn(4, setTrackDescription)
}
def setTrackDescription() {
	def submode = device.currentValue("submode")
	unschedule(setTrackDesciption)
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
			sendEvent(name: "trackDesctiption", value: "")
	}
	runIn(2, getPlayTime)
    runIn(10, getPlayStatus)
}
def getPlayTime() {
	if (device.currentValue("currentCp") == "TuneIn" || device.currentValue("currentCp") == "iHeartRadio") {
		log.info "${device.label} does not update TrackDescription for TuneIn nor iHeartRadio"
		return
	} else {
		def submode = device.currentValue("submode")
		sendCmd("/UIC?cmd=%3Cname%3EGetCurrentPlayTime%3C/name%3E")
    }
}
def schedSetTrackDescription() {
	def nextUpdate = device.currentValue("tracklength") - device.currentValue("playtime") + 3
	runIn(nextUpdate, setTrackDescription)
	log.debug "${device.label} Track Description will update in ${nextUpdate} seconds"
}
def setLabels() {
	switch(device.currentValue("submode")) {
		case "dlna":
			sendEvent(name: "shuffle", value: "off")
			sendEvent(name: "repeat", value: "off")
            break
		case "cp":
        	if (device.currentValue("currentCp") == "Amazon") {
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
	def submode = device.currentValue("submode")
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
	def submode = device.currentValue("submode")
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
def playPreset(player, music) {
	sendEvent(name: "currentPlayer", value: player)
	sendEvent(name: "currentMusic", value: music)
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
	def player = device.currentValue("currentPlayer")
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
def SetPlaySelect(contentId) {
	sendCmd("/CPM?cmd=%3Cname%3ESetPlaySelect%3C/name%3E" +
			"%3Cp%20type=%22dec%22%20name=%22selectitemid%22%20val=%22${contentId}%22/%3E")
	log.info "Playing preset channel ${device.currentValue("currentMusic")} on ${device.currentValue("currentPlayer")}"
	runIn(2, setTrackDescription)
}
def SetPlayPreset(contentId) {
	sendCmd("/CPM?cmd=%3Cname%3ESetPlayPreset%3C/name%3E" +
			"%3Cp%20type%3D%22dec%22%20name%3D%22presetindex%22%20val%3D%22${contentId}%22/%3E" +
			"%3Cp%20type%3D%22dec%22%20name%3D%22presettype%22%20val%3D%221%22/%3E")
	log.info "Playing preset channel ${device.currentValue("currentMusic")} on ${device.currentValue("currentPlayer")}"
	runIn(2, setTrackDescription)
}

//	----- TURN SHUFFLE/REPEAT CAPABILITY ON AND OFF -----
def inactive() {
	log.error "${device.label} The specific tile is not active due to mode of player."
}

def refresh() {
	getPlayStatus()
    getLevel()
    getMute()
    setLabels()
    getEqPresetName()
    getPwr()
    runIn(3, setTrackDescription)
}
def bogus() {
	sendCmd("/UIC?cmd=%3Cname%3EBogus%3C/name%3E")
}

//	----- SEND COMMAND TO SOUNDBAR -----
private sendCmd(command){
	def cmdStr = new physicalgraph.device.HubAction([
		method: "GET",
		path: command,
		headers: [
			HOST: "${deviceIP}:55001",
		]],
		null,
		[callback: parseResponse]
	)
	sendHubCommand(cmdStr)
}
//	----- PARSE RESPONSE DATA BASED ON METHOD -----
void parseResponse(resp) {
	def respMethod = (new XmlSlurper().parseText(resp.body)).method
	def respData = (new XmlSlurper().parseText(resp.body)).response
	switch(respMethod) {
//	----- SOUNDBAR STATUS METHODS -----
		case "PowerStatus":
			break
		case "CurrentFunc":
			sendEvent(name: "source", value: respData.function)
			sendEvent(name: "submode", value: respData.submode)
			log.info "${device.label} Source = ${device.currentValue("source")}, Submode = ${device.currentValue("submode")}"
			break
		case "VolumeLevel":
        	def scale = device.currentValue("volScale")
        	def level = respData.volume.toInteger()
        	level = Math.round(100*level/scale).toInteger()
			sendEvent(name: "level", value: level)
			log.info "${device.label} Volume set to ${device.currentValue("level")}"
			break
		case "MuteStatus":
			if (respData.mute == "on") {
				sendEvent(name: "mute", value: "muted")
			} else {
				sendEvent(name: "mute", value: "unmuted")
			}
			log.info "${device.label} Device Mute is set to ${device.currentValue("mute")}"
	   		break
		case "7BandEQList":
			sendEvent(name: "eqPresetCount", value: respData.listcount)
			break
        case "CurrentEQMode":
			sendEvent(name: "eqPreset", value: respData.presetname)
            sendEvent(name: "currentEqPreset", value: respData.presetindex)    
			log.info "${device.label} Equalizer Preset now ${device.currentValue("eqPreset")}"
            break
		case "RearLevel":
	   	sendEvent(name: "rearLevel", value: respData.level)
			log.info "${device.label} Rear Level is at ${device.currentValue("rearLevel")}."
			break
//	----- MEDIA CONTROL STATUS METHODS -----
		case "PlayStatus":
		case "PlaybackStatus":
			if (respData.playstatus == "play") {
				sendEvent(name: "status", value: "playing")
			} else if (respData.playstatus == "stop" || "pause" || "paused") {
				sendEvent(name: "status", value: "paused")
		   }
			log.debug "${device.label} Play Status is ${device.currentValue("status")}"
			break
		case "RepeatMode":
			def submode = device.currentValue("submode")
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
			log.info "${device.label} Repeat Mode set to ${device.currentValue("repeat")}"
			break
		case "ShuffleMode":
			sendEvent(name: "shuffle", value: respData.shuffle)
			log.info "${device.label} Shuffle Mode set to ${device.currentValue("shuffle")}"
			break
		case "ToggleShuffle":
			if (respData.shufflemode == "1") {
				sendEvent(name: "shuffle", value: "on")
			} else {
				sendEvent(name: "shuffle", value: "off")
			}
			log.info "${device.label} Shuffle Mode set to ${device.currentValue("shuffle")}"
			break
//	----- METHODS TO UPDATE TRACK DESCRIPTION -----
		case "MusicInfo":
			sendEvent(name: "trackDescription", value: "${respData.title}\n${respData.artist}")
			log.info "${device.label} trackDescription set to ${device.currentValue("trackDescription")}"
            break
		case "RadioInfo":
        	if (respData.cpname == "TuneIn" || respData.cpname == "iHeartRadio") {
				sendEvent(name: "trackDescription", value: respData.title)
            } else {
				sendEvent(name: "trackDescription", value: "${respData.artist}\n${respData.title}")
            }
            sendEvent(name: "currentCp", value: respData.cpname)
            sendEvent(name: "currentMusic", value: respData.title)
			log.info "${device.label} trackDescription set to ${device.currentValue("trackDescription")}"
			if (respData.tracklength != "" && respData.tracklength.toInteger() > 1) {
				sendEvent(name: "tracklength", value: respData.tracklength.toInteger())
			}
			break
		case "AcmMode":
			sendEvent(name: "trackDescription", value: respData.audiosourcename)
			log.info "${device.label} trackDescription set to ${device.currentValue("trackDescription")}"
			break
		case "MusicPlayTime":
			if (device.currentValue("submode") == "dlna") {
				sendEvent(name: "tracklength", value: respData.timelength.toInteger())
			}
            if (respData.playtime != ""){
				sendEvent(name: "playtime", value: respData.playtime.toInteger())
            }
            schedSetTrackDescription()
			break
//	----- TUNE PRESET METHODS -----
		case "CpChanged":
		case "RadioSelected":
		case "AmazonCpSelected":
			sendEvent(name: "currentCp", value: respData.cpname)
            setLabels()
            selectSubmenu()
			break
		case "RadioList":
			def contentId = ""
			def music = device.currentValue("currentMusic")
			def menuItems = respData.menulist.menuitem
			menuItems.each {
				if (contentId == "") {
					if (it.title == music) {
						contentId = it.contentid
						SetPlaySelect(contentId)
					}
				}
			}
			if (contentId == "") {
		 		log.error "${device.label} The station is not valid"
			}
			break
		case "PresetList":
			def contentId = ""
			def presets = respData.presetlist.preset
			def music = device.currentValue("currentMusic")
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
        	def hwType = respData.version.toString()
            hwType = hwType.substring(0,3)
            sendEvent(name: "hwType", value: hwType)
			log.info "${device.label} Hardware Type is ${device.currentValue("hwType")}"
            break
        case "EQMode":
		case "EndPlaybackEvent":
		case "MediaBufferEndEvent":
		case "StopPlaybackEvent":
		case "PausePlaybackEvent":
        case "MediaBufferStartEvent":
        case "StartPlaybackEvent":
        case "EQDrc":
        case "RequestDeviceInfo":
        	bogus()
            break
 		default:
        	log.debug "NEW METHOD:  ${respMethod}"
            log.debug "Associated Data:  ${respData}"
        	bogus()
        	break
	}
}