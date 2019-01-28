TuningGUI {
	var <>rootFreq, <ratios, <output;
	var synths;

	// GUI variables
	var window, inputRoot, inputOutput, buttons;

		*new {|rootFreq = 100, ratios, output = 0|
		^super.newCopyArgs(rootFreq, ratios, output).initTuningGUI;
	}

	initTuningGUI {
		var splitIntoRows = {| array |
			var rows = (array.size / 4).ceil.asInteger;

			var newArray = Array.fill(rows, {| i |
				var layout = [];
				array.do({| item, j |
					if((j >= (i*4))&&(j < ((i+1)*4)), {
						layout = layout.add(item)
					})
				});

				HLayout(*layout);
			});

			newArray;
		};


		
		synths = nil!ratios.size;

		SynthDef(\tuneSaw, {|freq=100, gate=1, out=0|
			var env = Env.asr(0.01,1,0.01).kr(2, gate) * 0.2;
			var sig = Saw.ar(freq, env);

			Out.ar(out, sig)
		}).add;

		window = Window("TuningGUI", Rect());

		buttons = ratios.collect({| i |
			Button()
			.states_([
				[
					i[0].asString ++ "/" ++ i[1].asString,
					Color.black,
					Color.white
				],
				[
					i[0].asString ++ "/" ++ i[1].asString,
					Color.black,
					Color.yellow
				]
			])
		});

		buttons.do{| item, i |
			item.action = {| button |
				var state = button.value;

				switch(state,
					0, {
						if(synths[i].notNil, {
							synths[i].set(\gate,0)})
					},
					1, {
						synths[i] = Synth(\tuneSaw, [
							\freq, rootFreq * ratios[i][0] / ratios[i][1],
							\out, output
						])
					},
				)

			}
		};
		
		buttons = splitIntoRows.value(buttons);

		window.layout = VLayout(
			HLayout(
				VLayout(
					StaticText().string_("Root freq:").align_(\center),
					inputRoot = TextField().string_(rootFreq.asString).align_(\center),
				),
				VLayout(
					StaticText().string_("Output:").align_(\center),
					inputOutput = PopUpMenu().items_(["0", "1"])
				)
			),
			VLayout(*buttons);
		);

		inputRoot.action = {| field |
			rootFreq = field.value.asInteger
		};
		inputOutput.action = {| field |
			output = field.value.asInteger
		};

		
	}

	show {
		window.front;
	}
}
