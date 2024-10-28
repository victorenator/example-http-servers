import socket
from http.server import BaseHTTPRequestHandler, HTTPServer

hostName = "::1"
serverPort = 8081
hello = bytes("Hello", "utf-8")

class MyHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        self.send_response(200)
        self.send_header("content-type", "text/plain")
        self.end_headers()
        self.wfile.write(hello)
        
class HTTPServer6(HTTPServer):
  address_family = socket.AF_INET6

if __name__ == "__main__":
    srv = HTTPServer6((hostName, serverPort), MyHandler)
    print("Server started http://%s:%s" % (hostName, serverPort))

    try:
        srv.serve_forever()
    except KeyboardInterrupt:
        pass

    srv.server_close()
