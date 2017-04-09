// map size
const MAP_SIZE = 500;

// Global reference to the canvas element.
let canvas;

// Global reference to the canvas' context.
let ctx;

let map;
let topleft;
let botright;
let scalex;
let scaley;
let twopoints = [];

$(document).ready(() => {

    // Setting up the canvas.
    canvas = $('#map')[0];
    canvas.width = MAP_SIZE
    canvas.height = MAP_SIZE

    // TODO: Set up the canvas context.
    ctx = canvas.getContext("2d");
    overlay = canvas.getContext("2d");
    console.log ("canvas created");

    topleft = [41.828163, -71.404871];
    botright = [41.825541, -71.400365];

    $.post("/getInitial", {"a": topleft[0], "b": topleft[1], "c": botright[0], "d": botright[1]}, responseJSON => {
            const responseObject = JSON.parse(responseJSON);
            map = responseObject.ways;
            scale(1);
            draw();
            console.log("hihi")
    });

    $('#map').click(pointOnClick);
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

    scalex = MAP_SIZE / Math.abs(topleft[1] - botright[1]);
    scaley = MAP_SIZE / Math.abs(topleft[0] - botright[0]);
}

function descaleX(x) {
	return x * 1/scalex;
}

function descaleY(y) {
	return y * 1/scaley;
}


function draw() {

    for (let way of map) {
        //console.log(way)
        const start = [parseFloat(way[1]), parseFloat(way[2])];
        const end = [parseFloat(way[3]), parseFloat(way[4])];

        ctx.moveTo(toPixelx(start), toPixely(start));
        ctx.lineTo(toPixelx(end), toPixely(end));
        
    }
    ctx.strokeStyle = 'black'
    ctx.stroke();
    
}

function highlight() {

    ctx.beginPath();
    for (let path of shortest) {
        const st = [parseFloat(path[0]), parseFloat(path[1])];
        const en = [parseFloat(path[2]), parseFloat(path[3])];
        console.log(st)
        console.log(en)
        ctx.moveTo(toPixelx(st), toPixely(st));
        ctx.lineTo(toPixelx(en), toPixely(en));
        
    }
    ctx.strokeStyle = 'yellow'
    ctx.stroke();
}

function clear() {
    for (let node of twopoints)
        console.log(node)
        ctx.clearRect(toPixelx(node), toPixely(node), 5, 5);
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
*/

const pointOnClick = event => {

	// Get the x, y coordinates of the click event
	// with (0, 0) being the top left corner of canvas.
    const x = event.pageX 
    const y = event.pageY

	const realX = topleft[1] + descaleX(x)
	const realY = topleft[0] - descaleY(y)

    $.post("/getNearest", {"lat": realY, "lon": realX}, responseJSON => {
        const responseObject = JSON.parse(responseJSON);
        coord = responseObject.point;
        const point = [parseFloat(coord["lat"]), parseFloat(coord["lon"])];
        if (point !== null) {
            twopoints.push(point)
        }
        console.log(twopoints)
        console.log(twopoints.length)

        ctx.fillStyle = "Red";
        ctx.fillRect(toPixelx(point), toPixely(point), 5, 5);

        if (twopoints.length === 2) {
            $.post("/getPathFromNode", {"a": twopoints[0][0], "b": twopoints[0][1], "c": twopoints[1][0], "d": twopoints[1][1]}, responseJSON => {
                const responseObject = JSON.parse(responseJSON);
                shortest = responseObject.ways;
                console.log(shortest)
                if (shortest === []) {
                    alert("Path doesn't exist!");
                }
                highlight();
            })
            setTimeout(clear, 3000);
            twopoints = [];
        }
    });

};

