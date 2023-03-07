module carisa/deployer

go 1.13

replace carisa/core => ../carisa-corego

require (
	carisa/core v0.0.0-00010101000000-000000000000
	github.com/imdario/mergo v0.3.7 // indirect
	github.com/labstack/echo/v4 v4.1.10
	github.com/stretchr/testify v1.4.0
	k8s.io/api v0.20.0-alpha.2
	k8s.io/apimachinery v0.20.0-alpha.2
	k8s.io/client-go v0.20.0-alpha.2
)
