const http = require('http');
const fs   = require('fs');
const path = require('path');

const PORT = 8080;
const DIR  = __dirname;

const MIME = {
  '.html':'text/html',
  '.css':'text/css',
  '.js':'application/javascript',
  '.png':'image/png',
  '.jpg':'image/jpeg',
  '.ico':'image/x-icon'
};

http.createServer((req, res) => {
  let filePath = path.join(DIR, req.url === '/' ? 'index.html' : req.url);
  const ext = path.extname(filePath);
  fs.readFile(filePath, (err, data) => {
    if (err) {
      res.writeHead(404); res.end('Not found');
      return;
    }
    res.writeHead(200, {'Content-Type': MIME[ext] || 'text/plain'});
    res.end(data);
  });
}).listen(PORT, '0.0.0.0', () => {
  console.log('ShopEase running at http://localhost:' + PORT);
});
