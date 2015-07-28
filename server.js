var ws = require('ws');

var server = new ws.Server({port: 8001});

var counter = 0;

setInterval(function() {
  counter += 1;
  console.log("interval");
  server.clients.forEach(function(conn) {
    console.log("sending");
    conn.send(counter.toString());
  });
}, 1000);

process.on('uncaughtException', function (err) {
  console.error(err.stack);
});
