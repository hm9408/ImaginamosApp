# ImaginamosApp
Esta aplicación es parte del proceso de selección de Imaginamos.com.
Sus funciones deben ser:

1. Cargar la lista de las 20 aplicaciones más descargadas del Apple AppStore, a partir de un JSON oficial descargado de su página web (https://itunes.apple.com/us/rss/topfreeapplications/limit=20/json).
2. Crear una vista principal con dicha lista, mostrando cada aplicación como un elemento.
3. Crear una vista en la que se muestre el detalle de la aplicación seleccionada.

## Restricciones
* Utilizar estrictamente solo las imágenes relativas que vienen en el JSON
* La aplicación debe funcionar offline y online.
* Aplicación universal (Smartphone y Tablet)
* Smartphone: orientación landscape
* Tablet: orientación portrait
* La vista del menú es requerida en grillas (Tablet), listas (Smartphone)
* Debe tener animaciones entre las vistas
* IDE: Android Studio
* Compatibilidad con Android 4+ (API Level 14+)

## Comentarios
* Se utilizaron las librerías oficiales de Google CardView y RecyclerView
* Se utilizó la librería Picasso para cargar los íconos de cada aplicación de manera asíncrona.
* Se utilizó un SwipeRefreshLayout en la AppListActivity, para poder actualizar la lista fácilmente.
* El método que actualiza la lista de aplicaciones sigue los siguientes pasos:
  1. Revisa si hay conexión a Internet. Si hay conexión a Internet, intenta descargar el archivo y actualizar la lista. Si no hay conexión a internet:
      1. Y existe el archivo, porque fue descargado anteriormente, carga la lista de manera offline.
      2. Y no existe el archivo, no carga la lista.
* Para mejorar el desempeño al pasar Extras en los Intents, la clase App (Modelo de la aplicación cargada) implementa la interfaz Parcelable.
* La aplicación fue probada en un OnePlus One ('bacon', Android 6.0.1, Unofficial CyanogenMod) y en una Nexus 7 2012 ('grouper', Android 5.1.1, Stock).
* El logo fue tomado de la página de LinkedIn de Imaginamos.

## Vistas
### OnePlus One test, AppListActivity (Orientation: landscape)
![alt text](http://i.imgur.com/ffQDfCP.png "OnePlus One test, AppListActivity (Orientation: landscape)")
### OnePlus One test, AppDetailActivity (Orientation: landscape)
![alt text](http://i.imgur.com/fo4XBRA.png "OnePlus One test, AppDetailActivity (Orientation: landscape)")
### Nexus 7 2012 test, AppListActivity (Orientation: portrait)
![alt text](http://i.imgur.com/Y0196FO.png "Nexus 7 2012 test, AppListActivity  (Orientation: portrait)")
### Nexus 7 2012 test, AppDetailActivity (Orientation: portrait) 
![alt text](http://i.imgur.com/qfqLmcP.png "Nexus 7 2012 test, AppDetailActivity (Orientation: portrait) ")
### Nexus 7 2012 test, updating list 
![alt text](http://i.imgur.com/gyMKZhW.png "Nexus 7 2012 test, updating list")
### Nexus 7 2012 test, updating list (offline mode) 
![alt text](http://i.imgur.com/kR29MqU.png "Nexus 7 2012 test, updating list (offline mode)")
