// class definition - sparas i User Library
// den ärver från Object-klassen - man kan skicka .new till klassen även om jag inte definerar den explicit i klassdefinitionen

// en asterisk betyder att det är en metod man kallar på klassen, inte på instansen.

// < betyder bara geter - här är tuning-variabeln en geter
// > betyder seter

MidiNoteToFreqInScale {
	var <tuning, <root;
	var scale;
	// vi gör en egen .new-metod som overridar den gamla
	*new {|myTuning = \et12, myRootKey = 60|
		// titta uppåt tills den hittar en .new-metod, kalla på metoden iniMidiNoteToFreqInScale, som finns längre ner
		^super.newCopyArgs(myTuning,myRootKey).initMidiNoteToFreqInScale;
		//^super.new.initMidiNoteToFreqInScale(myTuning, root);
	}

	initMidiNoteToFreqInScale {//|myTuning|
		//tuning = myTuning;
		scale = Scale.chromatic;
		scale.tuning_(tuning);
	}

	getFreq {|midinote|
		var freq;
		freq = (midinote-root).degreeToKey(scale);
		^(freq+60).midicps;
	}
}