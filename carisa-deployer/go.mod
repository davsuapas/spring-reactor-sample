module carisa/deployer

go 1.13

replace carisa/core => ../carisa-corego

require (
	carisa/core v0.0.0-00010101000000-000000000000
	github.com/imdario/mergo v0.3.7 // indirect
	github.com/labstack/echo/v4 v4.9.0
	github.com/spf13/pflag v1.0.5 // indirect
	github.com/stretchr/testify v1.7.0
	golang.org/x/oauth2 v0.0.0-20190604053449-0f29369cfe45 // indirect
	k8s.io/api v0.0.0-20190923155552-eac758366a00
	k8s.io/apimachinery v0.0.0-20190923155427-ec87dd743e08
	k8s.io/client-go v0.0.0-20190620085101-78d2af792bab
	k8s.io/utils v0.0.0-20190923111123-69764acb6e8e // indirect
)
