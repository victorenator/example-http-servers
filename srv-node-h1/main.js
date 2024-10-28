import {createServer} from 'node:http';

const HELLO = Buffer.from('hello');
const HELLO_HEADERS = {
  'content-type': 'text/plain',
};

const srv = createServer(async (req, res) => {
  res.writeHead(200, HELLO_HEADERS);
  res.end(HELLO);
});

srv.listen(8081, '::1');
