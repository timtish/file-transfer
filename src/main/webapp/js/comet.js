/* WebSocket connection */
ru.timtish.comet = {

    socket: null,
    connected: false,

    open: function (url) {
        if (ru.timtish.comet.socket) return;

        var socket = new WebSocket(url);

        socket.addEventListener("open", function (event) {
            this.connected = true;
        });

        socket.addEventListener("close", function (event) {
            this.connected = false;
        });

        // Display messages received from the server
        socket.addEventListener("message", function (event) {
            ru.timtish.bridge.alert("Server Says: " + event.data);
            // todo: add method ru.timtish.comet.addListener(event filter)
        });

        // Display any errors that occur
        socket.addEventListener("error", function (event) {
            ru.timtish.bridge.alert("Error: " + event);
        });

        this.socket = socket;
    },

    close: function () {
        this.socket.close();
        this.socket = null;
        this.connected = false;
    },

    check: function () {
        if (!this.socket) this.open();
    },

    send: function (o) {
        this.check();
        this.socket.send(o);
    }
}
