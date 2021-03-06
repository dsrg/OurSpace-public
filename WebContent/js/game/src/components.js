// The Grid component allows an element to be located
//  on a grid of tiles
Crafty.c('Grid', {
	init: function() {
		this.attr({
			w: Game.map_grid.tile.width,
			h: Game.map_grid.tile.height
		});
	},

	// Locate this entity at the given position on the grid
	at: function(x, y) {
		if (x === undefined && y === undefined) {
			return { x: this.x/Game.map_grid.tile.width, y: this.y/Game.map_grid.tile.height };
		} else {
			this.attr({ x: x * Game.map_grid.tile.width, y: y * Game.map_grid.tile.height });
			return this;
		}
	}
});

// An "Actor" is an entity that is drawn in 2D on canvas
//  via our logical coordinate grid
Crafty.c('Actor', {
	init: function() {
		this.requires('2D, Canvas, Grid');
	},
});

// A Tree is just an Actor with a certain sprite
Crafty.c('Tree', {
	init: function() {
		this.requires('Actor, Solid, spr_tree');
	},
});

// A Bush is just an Actor with a certain sprite
Crafty.c('Bush', {
	init: function() {
		this.requires('Actor, Solid, spr_bush');
	},
});

// A Rock is just an Actor with a certain sprite
Crafty.c('Rock', {
	init: function() {
    this.requires('Actor, Solid, spr_rock');
	},
});

// [chalin
/**@
* #MyFourway
* @category Input
* Based on Crafty#Fourway
*/
Crafty.c("MyFourway", {

	init: function () {
		this.requires("Multiway");
	},
	
//	_keydown: function (e) {
//			if (this._keys[e.key]) {
//				this._movement.x = Math.round((this._movement.x + this._keys[e.key].x) * 1000) / 1000;
//				this._movement.y = Math.round((this._movement.y + this._keys[e.key].y) * 1000) / 1000;
//				this.trigger('NewDirection', this._movement);
//			}
//	},
	
	_keyup: function (e) {
		// Ignore.
		// console.log('MyFourway._keyup: ignore');
	},

	/**@
	* #.myfourway
	* @see Multiway
	*/
	myfourway: function (speed) {
		// var role = $('#all').data('role') || 'SUBJECT1';
		var keys = {};
		/*{ UP_ARROW: -90,
			DOWN_ARROW: 90,
			RIGHT_ARROW: 0,
			LEFT_ARROW: 180,
			W: -90,
			S: 90,
			D: 0,
			A: 180,
			Z: -90,
			Q: 180 }
		*/
		this.multiway(speed, keys);
		return this;
	}
});
// chalin]

// This is the player-controlled character
Crafty.c('PlayerCharacter', {
	init: function() {
		this.requires('Actor, Multiway, MyFourway, Collision, spr_player, SpriteAnimation')
			.myfourway(2)
			.stopOnSolids()
			.onHit('Village', this.visitVillage)
			// These next lines define our four animations
			//  each call to .animate specifies:
			//  - the name of the animation
			//  - the x and y coordinates within the sprite
			//     map at which the animation set begins
			//  - the number of animation frames *in addition to* the first one
			.animate('PlayerMovingUp',    0, 0, 2)
			.animate('PlayerMovingRight', 0, 1, 2)
			.animate('PlayerMovingDown',  0, 2, 2)
			.animate('PlayerMovingLeft',  0, 3, 2);

		// Watch for a change of direction and switch animations accordingly
		var animation_speed = 4;
		this.bind('NewDirection', function(data) {
			// console.log('Game NewDirection: ' + data.x + ', ' + data.y);
			if (data.x > 0) {
				this.animate('PlayerMovingRight', animation_speed, -1);
			} else if (data.x < 0) {
				this.animate('PlayerMovingLeft', animation_speed, -1);
			} else if (data.y > 0) {
				this.animate('PlayerMovingDown', animation_speed, -1);
			} else if (data.y < 0) {
				this.animate('PlayerMovingUp', animation_speed, -1);
			} else {
				this.stop();
			}
		});
		// [chalin
		this.bind('Moved', function(data) {
			var loc = this.at();
			// console.log('Game Moved: [' + Crafty.timer.frame + '] '+ data.x + ', ' + data.y
			//		+ "; " + loc.x + ", " + loc.y + '; hit = ' + !!this.hit('Solid'));	
			var stop = false;
			// Check if the player has moved one full tile. If so, stop.
			if (this._movement.x !== 0 && this.x % 16 == 0) {
				this._movement.x = 0;
				stop = 'x';
			}
			if (this._movement.y !== 0 && this.y % 16 == 0) {
				this._movement.y = 0;
				stop = stop ? stop + '& y' : 'y';
			}
			if(!!stop) {
				// console.log('Game Moved: stopping in direction(s) ' + stop);
				this._speed = 0;
				this.trigger('NewDirection', this._movement);
			}
		});
		// this.bind('EnterFrame', function(data) {
		//	console.log('EnterFrame: ' + data.frame);
		// });
		// chalin]
	},

	// Registers a stop-movement function to be called when
	//  this entity hits an entity with the "Solid" component
	stopOnSolids: function() {
		this.onHit('Solid', this.stopMovement);
		return this;
	},

	// Stops the movement
	stopMovement: function() {
		// console.log('stopMovement: ' + this.x + ', ' + this.y);
		this._speed = 0;
		if (this._movement) {
			this.x -= this._movement.x;
			this.y -= this._movement.y;
			this._movement = {x:0, y:0};
			this.trigger('NewDirection', this._movement);
		}
		// console.log('stopMovement out: ' + this.x + ', ' + this.y);
	},

	// Respond to this player visiting a village
	visitVillage: function(data) {
		villlage = data[0].obj;
		villlage.visit();
	}
});

// A village is a tile on the grid that the PC must visit in order to win the game
Crafty.c('Village', {
	init: function() {
		this.requires('Actor, spr_village');
	},

	// Process a visitation with this village
	visit: function() {
		this.destroy();
		Crafty.audio.play('knock');
		Crafty.audio.play('ring');
		Crafty.trigger('VillageVisited', this);
	}
});