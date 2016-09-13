# [Tarea 3](https://github.com/josemalvarezg1/Procesamiento-Digital-de-Imagenes-Tarea-3)

## Tabla de contenido
* Desarrollo
* Integrantes

## Desarrollo
Esta tarea fue desarrollada en el lenguaje Java bajo el IDE NetBeans 8.1.

Puede ser ejecutada como un proyecto en el IDE NetBeans cargando la carpeta "ide" y añadiendo las bibliotecas que se encuentran en la carpeta lib. O puede ser ejecutada directamente desde la terminal de Linux estándose posicionado en la carpeta "bin" y luego ingresando el comando "java -jar Tarea3_25038805_24635907.jar"

Deberá cargar una imagen bitmap (formato .bmp) de 1, 4, 8, 16 o 24 bits desde el botón "Cargar Imagen". Puede seleccionar las operaciones deseadas desde el combo box. Una vez seleccionada la opción, se debe presionar el botón "Operar". Para las operaciones negativo, espejo horizontal, espejo vertical y ecualizar, el resultado será desplegado automáticamente, tanto en la imagen mostrada como en el histograma. Ahora, para las operaciones brillo, contraste, umbralización, rotar y zoom se deberá seleccionar un valor en el "slider" que se encuentra encima de los botones y luego presionar el botón "Operar". Para las operaciones de filtro como lo son Box Blur, Blur Gaussiano, Perfilado, Prewitt y Sobel se deberá seleccionar desde el "slider" el tamaño N del kernel o matriz de convolución y luego presionar el botón "Operar".
En el caso del escalamiento, al presionar el botón "Operar", se desplegarán dos campos que corresponden al nuevo ancho y nuevo alto que se debe ingresar.
En el caso de seleccionar la opción "Kernel Personalizado" junto con el tamaño N establecido en el "slider" se desplegará una nueva interfaz la cual solicitará al usuario que ingrese el kernel a su gusto e indique el factor de división.
Puede seleccionar el botón "Info" para obtener información sobre la imagen que se está tratando. Esta información será la de la imagen original.
El botón "Volver a la original" revierte todos los cambios realizados a la imagen de entrada.
Las operaciones deshacer y rehacer estan implementadas para un solo nivel.
Al presionar el botón "Guardar imagen" se abrirá un panel donde se debe ingresar tanto el nombre de la imagen que se va a guardar como la ruta de la misma.


## Integrantes

**José Manuel Alvarez García - CI 25038805**

**José Gregorio Castro Lazo - CI 24635907**