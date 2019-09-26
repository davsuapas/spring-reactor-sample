module carisa/deployer

go 1.13

replace carisa/core => ../carisa-corego

require (
	carisa/core v0.0.0-00010101000000-000000000000
	github.com/imdario/mergo v0.3.7 // indirect
	github.com/spf13/pflag v1.0.5 // indirect
	github.com/stretchr/testify v1.4.0
	golang.org/x/crypto v0.0.0-20190923035154-9ee001bba392 // indirect
	golang.org/x/net v0.0.0-20190923162816-aa69164e4478 // indirect
	golang.org/x/oauth2 v0.0.0-20190604053449-0f29369cfe45 // indirect
	golang.org/x/time v0.0.0-20190921001708-c4c64cad1fd0 // indirect
	k8s.io/api v0.0.0-20190923155552-eac758366a00 // indirect
	k8s.io/client-go v11.0.0+incompatible
	k8s.io/utils v0.0.0-20190923111123-69764acb6e8e // indirect
)
