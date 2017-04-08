// map size
const MAP_SIZE = 500;

// Global reference to the canvas element.
let canvas;

// Global reference to the canvas' context.
let ctx;

/*
// Global reference to a 2D array that contains
// the letters of the Boggle board.
let letterGrid;

// Converts a string representation of a Boggle board
// (e.g. oksr\ncngl\nmwhy\noovw) to a 2D array
const convertToMatrix = str => {
	const rows = str.split('\n');
	return rows.map( row => {
		const letters = row.split('');
		return letters.map(l => {
			return l === 'q' ? 'Qu' : l.toUpperCase();
		});
	});
};

*/
let map;
let topleft;
let botright;
let scalex;
let scaley;

$(document).ready(() => {

    // Setting up the canvas.
    canvas = $('#map')[0];
    canvas.width = MAP_SIZE
    canvas.height = MAP_SIZE

    // TODO: Set up the canvas context.
    ctx = canvas.getContext("2d");
    console.log ("canvas created");

    topleft = [41.828163, -71.404871];
    botright = [41.825541, -71.400365];

    map = 
    {
        "brown st": [41.827867, -71.403149, 41.827076, -71.403042],

        "waterman st": [41.827076, -71.403042, 41.826900, -71.403200]

    };

    scale(1);
    draw();

    // $.post("/getWays", {"start": [41.828163, -71.404871], "end": [41.825541, -71.400365]}, responseJSON => {
    //         const responseObject = JSON.parse(responseJSON);
    //         map = responseObject;
    //         console.log(map);  
    // });
    const a = JSON.stringify(["41.828163", "-71.404871", "41.825541", "-71.400365"]);
    $.post("/getWays", {"a": 41.828163, "b": -71.404871, "c": 41.825541, "d": -71.400365}, responseJSON => {
            const responseObject = JSON.parse(responseJSON);
            map = responseObject;
            console.log(map);  
    });
});


function toPixelx(node) {
    return (node[1] - topleft[1]) * scalex;
}

function toPixely(node) {
    return (topleft[0] - node[0]) * scaley;
}

function scale(n) {
    topleft[0] = topleft[0] - (1 - n) / 2.0 * (topleft[0] - botright[0]);
    topleft[1] = topleft[1] + (1 - n) / 2.0 * (botright[1] - topleft[1]);

    botright[0] = botright[0] + (1 - n) / 2.0 * (topleft[0] - botright[0]);
    botright[1] = botright[1] - (1 - n) / 2.0 * (botright[1] - topleft[1]);

    scalex = 500.0 / Math.abs(topleft[1] - botright[1]);
    scaley = 500.0 / Math.abs(topleft[0] - botright[0]);
}

function draw() {

    for (let wayName in map) {
        const way = map[wayName];
        const start = [way[0], way[1]];
        const end = [way[2], way[3]];

        ctx.moveTo(toPixelx(start), toPixely(start));
        ctx.lineTo(toPixelx(end), toPixely(end));
        
    }
    ctx.stroke();
    
}
/*

Paints the boggle board.

const paintBoard = () => {

	// Setting the context's font and lineWidth.
	// Feel free to play around with this!
    ctx.font = '30px Andale Mono';
    ctx.lineWidth = 1;

    // TODO: Fill the background color of the canvas element
    // to something other than white using ctx.fillStyle and
    // ctx.fillRect().

    ctx.fillStyle = "pink";
    ctx.fillRect(0, 0, canvas.width, canvas.height);

    // TODO: Draw the grid lines for the boggle board using
    // ctx.beginPath(), ctx.moveTo(), ctx.lineTo(), and ctx.stroke().
    // It should look like a 4x4 board after you're done drawing the lines.
    // It might be helpful to use BOARD_SIZE and TILE_SIZE here.

    ctx.beginPath();

    for (let i = 0; i <= TILE_SIZE; i++) {
    	ctx.moveTo(0, i * TILE_SIZE);
    	ctx.lineTo(canvas.width, i*TILE_SIZE);
    }

    for (let i  = 0; i <= TILE_SIZE; i++) {
    	ctx.moveTo(i * TILE_SIZE, 0);
 		ctx.lineTo(i * TILE_SIZE, canvas.height);
 	}
 	ctx.stroke()

    // TODO: Draw the letters onto the boggle board using ctx.fillText().
    // The letter values are stored in letterGrid, which is a 4x4 array.

    for (let i = 0; i < letterGrid.length; i++) {
    	for (let j = 0; j < letterGrid.length; j++) {
    		ctx.fillStyle = "black";
    		ctx.fillText(letterGrid[i][j], j*TILE_SIZE+50, (i+1)*TILE_SIZE-50);
    	}
    }


};


	Called when the board is clicked.
	This function does two things if the click was valid:
	- Paints the clicked square on the board
	- Draws a path from the previous click to the current click
const paintOnClick = event => {

	// Get the x, y coordinates of the click event
	// with (0, 0) being the top left corner of canvas.
    const x = event.pageX - canvas.offsetLeft;
    const y = event.pageY - canvas.offsetTop;

    // TODO: Use these x, y coordinates to determine
    // the row and column of the clicked tile.
    const col = Math.floor(x / 100);
    const row = Math.floor(y / 100);
 
    const currPosition = new Position(row, col);
    const lastPosition = positions[positions.length-1];

    if (positions.length === 0 || isValidClick(lastPosition, currPosition)) {

    	const letter = letterGrid[row][col];

    	// TODO: Using the row and col variables, change the color of the
    	// clicked tile with ctx.fillRect() and ctx.fillText().

    	ctx.fillStyle = "Green";
    	ctx.fillRect(col*TILE_SIZE, row*TILE_SIZE, 100, 100);
    	ctx.fillStyle = "white";
    	ctx.fillText(letterGrid[row][col], col*TILE_SIZE+50, (row+1)*TILE_SIZE-50);


	    ctx.lineWidth = 10;

	    // Drawing the path from the previous click to the current click.
    	if (positions.length === 0) { // Is first click
    		ctx.beginPath();
	        ctx.moveTo(x, y);
	        currWord += letter.toLowerCase();
	        positions.push(currPosition);
	    } else if (isValidClick(lastPosition, currPosition)) {
	        ctx.lineTo(x, y);
	        ctx.stroke();
	        currWord += letter.toLowerCase();
	        positions.push(currPosition);
	    }

    }
}
*/

