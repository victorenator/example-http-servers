import {createServer} from 'node:http2';

const HELLO = Buffer.from('hello');
const HEADERS = {
  ':status': '200',
  'content-type': 'text/plain',
};

const srv = createServer();
srv.on('stream', stream => {
  stream.respond(HEADERS);
  stream.end(HELLO);
});

srv.listen(8082, '::1');
