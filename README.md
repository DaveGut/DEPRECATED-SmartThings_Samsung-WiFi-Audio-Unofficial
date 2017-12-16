<img src="https://github.com/DaveGut/Samsung-Multiroom-WiFi-Soundbar-SmartThings-Integration/blob/master/Screenshot.jpg" align="right"/>

# Samsung-WiFi-Soundbar-Speaker-with-SmartThings-Integration

This device handler controls a stand-alone Samsung WiFi soundbar or speaker.  It has been tested on the Samsung HS-MS650 Soundbar and the R1 Speaker.  It is expected to run on any of the Samsung Multiroom compatible speakers.  If not, I will work to upgrade.


# Capabilities

a.  Music playback control

b.  Device control (volume, mute, audio source, equalizer preset)

c.  8 Presets.  Ability to set-up and then call up presets for channel or music playback.

d.  3 Group Presets per speaker.

This device handler for SmartThings integration was completed using the commands from Port 55001, as defined at the site "https://sites.google.com/site/moosyresearch/projects/samsung_shape".  My appreciation for the many participants in that effort for their instrumental research and discoveries.

# Installation Instruction

Included in Documentation folder.

# Preset Management

Preset Management has been added to the program that allows the use to add and delete presets (with some limitations.
I.	TO ADD A PRESET

    a.	Find a preset with identification "vacant"
    b.	Have the channel/playlist playing on the speaker.  Path limitations:
        1.	Amazon.  playlists in "Playlists".
        2.	Amazon Prime.  Playlists in "Playlists" or in <"My Music", "Playlists">
        3.	iHeartRadio.  Channels in the "Favorites" folder.
        4.	Pandora.  Stations aready at the top level.
        6.	TuneIn.  No Limitation.
        7.	8tracks. No Limitation.
        8.	Other content players.  Programed for default, w/o limitation.  May not work.  If it works, great.  If not, contact author.
    c.	Press the "vacant" preset tile. Text will change to "Add Preset?"
    d.	Press the preset tile again.  Text will change to "updating" followed by title.
    e.	To not add the preset, do NOT press the preset tile a second time.
    
II.	TO ADD A GROUP PRESET

    a.	Set up the group, stereo, or surround system in the MultiRoom App.
    b.	Press the desired preset button (will be “- - - - -“).
    c.	Press the button again.
    d.	The preset will be active in SmartThings once created.
[Note.  To stop a preset, press the tile with “Active” in the first line.  You will have to press this tile twice to confirm turning off.]

III.  TO DELETE A PRESET.

    a.	Press the "Delete Preset" tile.  Text will change to "SELECT PRESET TO DELETE".  If you press this, the process will abort.
    b.	Press the preset you want to delete.  Text on the Delete Preset tile will change to "PRESS TO DELETE preset_n".
    c.	Press the Delete Preset tile again (within 10 seconds) to delete the preset.
