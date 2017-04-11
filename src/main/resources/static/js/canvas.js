// map size
const MAP_SIZE = 500;

// Global reference to the canvas element.
let canvas;

// Global reference to the canvas' context.
let ctx;

let map = {};
let topleft;
let botright;
let scalex;
let scaley;

let center = [41.826891, -71.402993];

let paths = [];
let startingTemp;
let endTemp;
let gettingPathFromStreet = false;

let traffic = {};
let trafficPaths = [];

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

    scale(1);
    draw();

    $('#map').mouseup(pointOnClick);
    $('form').submit(getPathFromSt)
    $("textarea").on('keyup', printSuggestions)
         
    window.setInterval(function(){
        getTraffic();  
    }, 1000);
});



$('html, body').css({
    overflow: 'hidden',
    height: '100%'
});

let dragFlag = 0;
let myPageX;
let myPageY;
$( "#map" ).mousemove(function( e ) {
    if(e.buttons == 1 && Math.abs(e.pageX - myPageX) > 1 && Math.abs(e.pageY - myPageY) > 1) {
        dragFlag = 1;

        topleft[1] = topleft[1] - descaleX(e.pageX - myPageX)
        botright[1] = botright[1] - descaleX(e.pageX - myPageX)

        topleft[0] = topleft[0] + descaleY(e.pageY - myPageY)
        botright[0] = botright[0] + descaleY(e.pageY - myPageY)

        myPageX = e.pageX
        myPageY = e.pageY

        draw()
    }
});

$( "#map" ).mousedown(function( e ) {
    dragFlag = 0;
    myPageX = e.pageX
    myPageY = e.pageY
});

$('#map').bind('mousewheel', function (e) {
    scale(1 - e.originalEvent.wheelDelta / 120 / 50);
    draw()
});

$('#clearPath').click(function (e) {
    if((startingTemp && endTemp) || gettingPathFromStreet) {
        alert("Still Navigating!")
    } else {
        startingTemp = undefined;
        endTemp = undefined;
        paths = [];
        draw();
    }
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
    ctx.clearRect(0, 0, 500, 500);

    let topx = Math.floor((topleft[1] - center[1]) / 0.01);
    let topy = Math.floor((topleft[0] - center[0]) / 0.01);

    let botx = Math.floor((botright[1] - center[1]) / 0.01);
    let boty = Math.floor((botright[0] - center[0]) / 0.01);

    ctx.beginPath()
    for(let x = topx; x <= botx; x++) {
        for(let y = topy; y >= boty; y--) {
            const key = x+","+y;
            if(map[key] && map[key] != "loading") {
                for (let way of map[key]) {
                    const start = [parseFloat(way[1]), parseFloat(way[2])];
                    const end = [parseFloat(way[3]), parseFloat(way[4])];
                    
                    if(traffic[way[0]]){
                        //console.log(parseFloat(traffic[way[0]]));
                        //if(parseFloat(traffic[way[0]]) > 5) {
                            trafficPaths.push(way);
                            //console.log("wooow")
                        //}
                    }

                    ctx.moveTo(toPixelx(start), toPixely(start));
                    ctx.lineTo(toPixelx(end), toPixely(end));
                }
            } else {
                getWays(x, y);
            }
        }
    }
    ctx.strokeStyle = 'black'
    ctx.lineWidth = 1;
    ctx.closePath();
    ctx.stroke();

    highlightTraffic();
    highlightPaths();
}

function highlightTraffic() {
    // console.log("traffic highlight")
    // console.log(trafficPaths)
    
    for (let way of trafficPaths) {
        ctx.beginPath();
        const start = [parseFloat(way[1]), parseFloat(way[2])];
        const end = [parseFloat(way[3]), parseFloat(way[4])];
        //console.log(start + ", " + end)
        ctx.moveTo(toPixelx(start), toPixely(start));
        ctx.lineTo(toPixelx(end), toPixely(end));
        //ctx.strokeStyle = 
        ctx.strokeStyle = "#"+getColor(parseFloat(traffic[way[0]]) / 10.0);
        ctx.lineWidth = 3;
        ctx.closePath();
        ctx.stroke();
    }
    
    trafficPaths = [];
}

function getColor(ratio) {
    var color1 = 'FF0000';
    var color2 = '99ff33';
    var hex = function(x) {
        x = x.toString(16);
        return (x.length == 1) ? '0' + x : x;
    };

    var r = Math.ceil(parseInt(color1.substring(0,2), 16) * ratio + parseInt(color2.substring(0,2), 16) * (1-ratio));
    var g = Math.ceil(parseInt(color1.substring(2,4), 16) * ratio + parseInt(color2.substring(2,4), 16) * (1-ratio));
    //var b = Math.ceil(parseInt(color1.substring(4,6), 16) * ratio + parseInt(color2.substring(4,6), 16) * (1-ratio));
    var b = 0;

    return hex(r) + hex(g) + hex(b);
}

function getWays(x, y) {
    const key = x+","+y;
    if(map[key] != "loading") {
        map[key] = "loading";
        $.post("/getWaysInBox", {"a": center[0] + 0.01 * (y + 1), "b": center[1] + 0.01 * x, 
            "c": center[0] + 0.01 * y, "d": center[1] + 0.01 * (x + 1)}, responseJSON => {
                const responseObject = JSON.parse(responseJSON);
                map[key] = responseObject.ways;
                console.log("loaded:" + "("+x+", "+y+")" + key);
                draw();
            });
    }
}

function highlightPaths() {
    if(startingTemp) {
        ctx.fillStyle = "Red";
        ctx.fillRect(toPixelx(startingTemp), toPixely(startingTemp), 10, 10);
    }    

    if(endTemp) {
        ctx.fillStyle = "#09F";
        ctx.fillRect(toPixelx(endTemp), toPixely(endTemp), 10, 10);
    } 

    if ((startingTemp && endTemp) || gettingPathFromStreet) {
        ctx.font = "20px Verdana";
        // Create gradient
        var gradient = ctx.createLinearGradient(350, 0, 480, 0);
        gradient.addColorStop("0", "magenta");
        gradient.addColorStop("0.5", "blue");
        gradient.addColorStop("1.0", "red");
        // Fill with gradient
        ctx.fillStyle = gradient;
        ctx.fillText("Navigating...", 350, 480);
    }

    //ctx.save();
    ctx.beginPath();
    //ctx.setLineDash([20, 10])
    for (let path of paths) {
        const st = [parseFloat(path[0]), parseFloat(path[1])];
        const en = [parseFloat(path[2]), parseFloat(path[3])];
        ctx.moveTo(toPixelx(st), toPixely(st));
        ctx.lineTo(toPixelx(en), toPixely(en));
        
    }
    ctx.strokeStyle = '#1E90FF'
    ctx.lineWidth = 5;
    ctx.closePath();
    ctx.stroke();
    //ctx.restore();
}

function getPathFromSt() {

    let str1 = $("#st1").val();
    let str2 = $("#st2").val();
    let str3 = $("#st3").val();
    let str4 = $("#st4").val();

    gettingPathFromStreet = true;
    $.post("/getPathFromNode", {"a": str1, "b": str2, "c": str3, "d": str4}, responseJSON => {
        const responseObject = JSON.parse(responseJSON);
        gettingPathFromStreet = false;
        if (responseObject.ways.length === 0) {
            alert("Path doesn't exist!");
        } else {
            paths = paths.concat(responseObject.ways);
            draw();
        }
    })
    draw();
}

function printSuggestions(event) {
    let word = event.target.value;
    $("#candi").empty();
    $.post("/suggestion", {text: word}, responseJSON => {
        const responseObject = JSON.parse(responseJSON);

        for (let i = 0; i < responseObject.options.length; i++) {
            let html = "<p>"+ responseObject.options[i] + "</p>";
            $("#candi").append(html);
        }

        if (word.length == 0) {
            $("#candi").empty();
        }
    });
}   

function getTraffic() {
    $.post("/getTraffic", {"a": "a"}, responseJSON => {
        const responseObject = JSON.parse(responseJSON);
        traffic = responseObject;
        draw();
    });
}   

const pointOnClick = event => {
    if (dragFlag === 0) {
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
            if (!startingTemp) {
                startingTemp = point;
            } else if (!endTemp) {
                endTemp = point;
                $.post("/getPathFromNode", {"a": startingTemp[0], "b": startingTemp[1], "c": point[0], "d": point[1]}, responseJSON => {
                    const responseObject = JSON.parse(responseJSON);
                    if (responseObject.ways.length === 0) {
                        alert("Path doesn't exist!");
                    } else {
                        paths = paths.concat(responseObject.ways);
                    }
                    startingTemp = undefined;
                    endTemp = undefined;
                    draw();
                });  
            }
            draw();
        });
    }
};

