const HELLO = new Blob(['Hello'], {type: 'text/plain'});

Deno.serve({
    port: 8081,
    hostname: '::1',
}, () => {
    return new Response(HELLO);
});
