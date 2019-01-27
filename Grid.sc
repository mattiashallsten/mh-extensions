Grid {
	var type, <>color, <rows, <cols, <rowOffset, <colOffset;



	var <midiout, padArray, gridRead, midiUID;

	var masterGrid;

	*new {|type = 'launchpad-s', color = 127, rows = 8, cols = 8, rowOffset = 0, colOffset = 0|
		^super.newCopyArgs(type, color, rows, cols, rowOffset, colOffset).initGrid
	}

	initGrid {
		~rowFunc = {|array2d|
			var newArray = [];

			array2d.rowsDo{|item, index|
				var array = [];

				item.do{|i,j|
					if((j >= colOffset) && (j < (cols + colOffset)), {
						array = array.add(i)
					});
				};

				if((index >= rowOffset) && (index < (rows + rowOffset)), {
					newArray = newArray ++ array
				});
			};

			array2d = Array2D.fromArray(rows, cols, newArray);
		};

		if( MIDIClient.initialized != true, {
			MIDIClient.init});

		MIDIIn.connectAll;

		case
		{type == 'launchpad-s'} {
			midiUID = MIDIIn.findPort("Launchpad S", "Launchpad S").asMIDIInPortUID;

			masterGrid = Array2D.fromArray(rows,cols,0!64);
			midiout = MIDIOut.newByName("Launchpad S", "Launchpad S").latency_(0.01);
			padArray = Array2D.fromArray(8,8, [
				112, 113, 114, 115, 116, 117, 118, 119,
				96, 97, 98, 99, 100, 101, 102, 103,
				80, 81, 82, 83, 84, 85, 86, 87,
				64, 65, 66, 67, 68, 69, 70, 71,
				48, 49, 50, 51, 52, 53, 54, 55,
				32, 33, 34, 35, 36, 37, 38, 39,
				16, 17, 18, 19, 20, 21, 22, 23,
				0, 1, 2, 3, 4, 5, 6, 7
			]);

			/*padArray = Array2D.fromArray(8,8,
			Array.fill(64, {|i|
			var row = (i / 8).floor.asInteger;
			(i + (row * 8)).linlin(0,119,119,0).asInteger;
			}));*/
		}
		{type == 'push-2'} {
			midiUID = MIDIIn.findPort("Ableton Push 2", "User Port").asMIDIInPortUID;

			masterGrid = Array2D.fromArray(rows,cols,0!64);
			midiout = MIDIOut.newByName("Ableton Push 2", "User Port").latency_(0.01);
			padArray = Array2D.fromArray(8,8, [
				36, 37, 38, 39, 40, 41, 42, 43,
				44, 45, 46, 47, 48, 49, 50, 51,
				52, 53, 54, 55, 56, 57, 58, 59,
				60, 61, 62, 63, 64, 65, 66, 67,
				68, 69, 70, 71, 72, 73, 74, 75,
				76, 77, 78, 79, 80, 81, 82, 83,
				84, 85, 86, 87, 88, 89, 90, 91,
				92, 93, 94, 95, 96, 97, 98, 99
			]);
		};

		padArray = ~rowFunc.value(padArray);
		//padArray = ~colFunc.value(padArray);




	}

	set { | row, col, val, light = 'on' |
		var num = padArray[row, col];
		masterGrid[row, col] = val;

		if(light == 'on', {
			midiout.noteOn(0, num, val * color)
		});
	}

	toggle { | light = 'on', function |
		var lightFuncOn = MIDIFunc.noteOn({|val,num,chan,src|
			if(src == midiUID, {
				var row, col, state;

				padArray.do{|x,y|
					if(num == x, {
						row = (y / 8).floor.asInteger;
						col = (y % 8).asInteger;
					})
				};


				state = 1 - masterGrid[row, col];

				this.set(row, col, state, light);

				if(function.notNil, {function.value(row,col,state)});
			})
		}, padArray);
	}

	momentary { | light = 'on', function |

		var lightFuncOn = MIDIFunc.noteOn({|val,num,chan,src|
			if(src == midiUID, {
				var row, col, state;

				padArray.do{|x,y|
					if(num == x, {
						row = (y / 8).floor.asInteger;
						col = (y % 8).asInteger;
					})
				};

				state = 1;

				this.set(row, col, state, light);


				if(function.notNil, {function.value(row,col,state)});
			});
		}, padArray);

		var lightFuncOff = MIDIFunc.noteOff({|val,num,chan,src|
			if(src == midiUID, {
				var row, col, state;

				padArray.do{|x,y|
					if(num == x, {
						row = (y / 8).floor.asInteger;
						col = (y % 8).asInteger;
					})
				};

				state = 0;

				this.set(row, col, state, light);

				if(function.notNil, {function.value(row,col,state)});
			})
		}, padArray);

	}

	/*animate { | type = 'cross', function |

		var light = 'on';
		var lightFuncOn, lightFuncOff;

		case
		{type == 'cross'} {
			lightFuncOn = MIDIFunc.noteOn({|val,num,chan,src|
				if(src == midiUID, {
					var row, col, state;
					var left, right, top, bottom;

					padArray.do{|x,y|
						if(num == x, {
							row = (y / 8).floor.asInteger;
							col = (y % 8).asInteger;
						})
					};

					state = 1;

					this.set(row, col, state, light);

					left = col;
					right = cols - col - 1;

					bottom = row;
					top = rows - row - 1;

					right.do{|i|
						fork {
							wait(0.2 * (i + 1));
							midiout.noteOn(0, padArray[row, (col + 1 + i).asInteger], 80);
							wait(0.1 * (i + 1));
							midiout.noteOff(0, padArray[row, (col + 1 + i).asInteger], 0);
						};
					};

					left.do{|i|
						fork {
							wait(0.2 * (i + 1));
							midiout.noteOn(0, padArray[row, (col -1 - i).asInteger], 80);
							wait(0.1 * (i + 1));
							midiout.noteOff(0, padArray[row, (col -1 - i).asInteger], 0);
						};
					};

					bottom.do{|i|
						fork {
							wait(0.2 * (i + 1));
							midiout.noteOn(0, padArray[(row - 1 - i).asInteger, col], 80);
							wait(0.1 * (i + 1));
							midiout.noteOff(0, padArray[(row - 1 - i).asInteger, col], 0);
						};
					};

					top.do{|i|
						fork {
							wait(0.2 * (i + 1));
							midiout.noteOn(0, padArray[(row + 1 + i).asInteger, col], 80);
							wait(0.1 * (i + 1));
							midiout.noteOff(0, padArray[(row + 1 + i).asInteger, col], 0);
						};
					};


					if(function.notNil, {function.value(row,col,state)});
				});
			}, padArray);
		};

		lightFuncOff = MIDIFunc.noteOff({|val,num,chan,src|
			if(src == midiUID, {
				var row, col, state;

				padArray.do{|x,y|
					if(num == x, {
						row = (y / 8).floor.asInteger;
						col = (y % 8).asInteger;
					})
				};

				state = 0;

				this.set(row, col, state, light);

				if(function.notNil, {function.value(row,col,state)});
			})
		}, padArray);

	}*/


	postGrid {
		masterGrid.postln;
	}

}