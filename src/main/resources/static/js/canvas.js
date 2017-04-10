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
    $('form').submit(getPathFromSt)
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
        ctx.moveTo(toPixelx(st), toPixely(st));
        ctx.lineTo(toPixelx(en), toPixely(en));
        
    }
    ctx.strokeStyle = 'red'
    ctx.stroke();
}

function clear(nodes) {
    for (let pathtwo of nodes) {
        const squ = [parseFloat(pathtwo[0]), parseFloat(pathtwo[1])];
        ctx.clearRect(toPixelx(squ), toPixely(squ), 5, 5);
    }
}

function getPathFromSt() {
    
    let str1 = $("#st1").val();
    let str2 = $("#st2").val();
    let str3 = $("#st3").val();
    let str4 = $("#st4").val();

    console.log("hi1")
    $.post("/getPathFromNode", {"a": str1, "b": str2, "c": str3, "d": str4}, responseJSON => {
        const responseObject = JSON.parse(responseJSON);
        console.log("hi2")
        shortest = responseObject.ways;
        console.log(shortest)
        if (shortest.length === 0) {
            alert("Path doesn't exist!");
        }
        highlight();
    })

}

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
        ctx.fillStyle = "Red";
        ctx.fillRect(toPixelx(point), toPixely(point), 5, 5);

        if (twopoints.length === 2) {
            $.post("/getPathFromNode", {"a": twopoints[0][0], "b": twopoints[0][1], "c": twopoints[1][0], "d": twopoints[1][1]}, responseJSON => {
                const responseObject = JSON.parse(responseJSON);
                shortest = responseObject.ways;
                if (shortest.length === 0) {
                    alert("Path doesn't exist!");
                }
                highlight();
            })
            //time sleep not working
            //setTimeout(clear(twopoints), 5000);
            twopoints = [];
        }
    });

};

