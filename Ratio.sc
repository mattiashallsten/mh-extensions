Ratio {
	var <num, <den, <description, <fav, <ratioAsString;
	var <value, <limit;

	*new {|num = 1, den = 1, description = "", fav = 0, ratioAsString|
		^super.newCopyArgs(num, den, description, fav, ratioAsString).initRatio
	}

	initRatio {
		if(ratioAsString.notNil, {
			if(ratioAsString.contains("/"), {
				#num, den = ratioAsString.split($/).collect{|i| i.asInteger};
			});
		});
			
		if(den != 0, {
			value = num / den;
			limit = this.getLimit(num, den);
		}, {
			"Illegal denominator!".postln;
			value = 1
		});
	}

	print {
		var str = num.asString ++ "/" ++ den.asString;

		^str
	}

	getLimit { | num, den |
		var numLimit = num.factors.reverse[0];
		var denLimit = num.factors.reverse[0];

		if(numLimit > denLimit, {
			^numLimit
		}, {
			^denLimit
		});
	}
}