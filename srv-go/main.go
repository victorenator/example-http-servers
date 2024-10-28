package main

import (
	"net/http"
)

func main() {
	hello := []byte("Hello")
	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		w.Write(hello)
	})

	http.ListenAndServe(":8081", nil)
}
