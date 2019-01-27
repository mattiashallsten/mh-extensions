MIDIdelay {
	var <midiout, <>delay, <>transpose, <>repeats, <outChan, <inChan;

	*new {|midiout = 0, delay = 0.5, transpose = 0, repeats = 2, outChan = 0, inChan = 0|
		^super.newCopyArgs(midiout, delay, transpose.asArray, repeats, outChan, inChan).initMIDIdelay;
	}

	// outside the class instead
	initMIDIdelay {
		MIDIClient.init;
		MIDIIn.connectAll;
	}

	// change to MIDIfuncs
	run {
		MIDIdef.noteOn(\noteOnDelay, {|val,num,chan|
			var i = 0;
			if(chan==inChan, {

				fork {
					delay.wait;
					num = num + transpose[i%transpose.size];
					num = num.clip(0,127);
					val = val - 10;
					val = val.clip(0,127);

					midiout.noteOn(outChan, num, val);
					i = i + 1;

					if((i < repeats) && (num < 110) && (num > 0), {
						thisFunction.value(val,num,chan)
					})
				}
			})
		});

		MIDIdef.noteOff(\noteOffDelay, {|val,num,chan|
			var i = 0;

			if(chan==inChan, {

				fork {
					delay.wait;
					num = num + transpose[i%transpose.size];
					num = num.clip(0,127);

					midiout.noteOff(outChan, num, val);
					i = i + 1;

					if((i < repeats) && (num < 110), {
						thisFunction.value(val,num,chan)
					})
				}
			})
		})
	}

	free {
		MIDIdef.noteOn(\noteOnDelay, {|val,num,chan|});
		MIDIdef.noteOff(\noteOffDelay, {|val,num,chan|});
	}
}