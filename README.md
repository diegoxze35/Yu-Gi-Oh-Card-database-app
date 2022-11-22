# Yu-Gi-Oh-Card-database-app
App realizada con fines de practica en el desarrollo móvil, usando MVVM, retrofit, corrutinas y Room

Yu-Gi-Oh! Card database
App que permite buscar información del juego de cartas coleccionables. Utilizando una arquitectura MVVM y Single Activity
![architecture](https://user-images.githubusercontent.com/115143423/202417057-1bd73303-d18e-45f2-a453-4886a4c28d60.png)

Al ser Single Activity el ViewModel los fragmentos pueden comunicarse gracias al viewModel
El viewModel se le injecta por constructor una clase NetworkConnectivity, dicha clase extiende de LiveData<Boolean> la cual contiene un objeto que 
hereda de la clase NetworkCallback, actualizando el valor con los callbacks onAvilable y onLost, validando la conexión de ese objeto Network estableciendo
una conexción con https://www.google.com

## Fragmento principal
Muestra cartas aleatorias, mostrandolas en un recyclerview y actualizando ese listado cada que se hace scroll
![Screenshot_20221117-040711_Yu-Gi-Oh! Card Database](https://user-images.githubusercontent.com/115143423/202418155-bcb8288e-8656-47f1-b4f6-0a1e8a698313.jpg)
![Screenshot_20221117-040722_Yu-Gi-Oh! Card Database](https://user-images.githubusercontent.com/115143423/202418163-99979532-7e1e-4aef-b6da-85835960f53b.jpg)
![Screenshot_20221117-041419_Yu-Gi-Oh! Card Database](https://user-images.githubusercontent.com/115143423/202420720-d9ad2c6b-17c6-468c-8257-218d59cb8654.jpg)

## Fragmento de detalle
![Screenshot_20221117-041403_Yu-Gi-Oh! Card Database](https://user-images.githubusercontent.com/115143423/202420839-5b6591b2-951a-491d-8a96-f3b58bf420a8.jpg)
![Screenshot_20221117-041405_Yu-Gi-Oh! Card Database](https://user-images.githubusercontent.com/115143423/202420841-b50e35c7-d766-411b-81c4-c96670b92ce3.jpg)
![Screenshot_20221117-042143_Yu-Gi-Oh! Card Database](https://user-images.githubusercontent.com/115143423/202421204-5a841eaf-ba64-45ef-a176-7e9238fd6454.jpg)

# Busqueda
La Appbar es administrada por la actividad, en el fragmento principal, cuenta con un icono, un searchView y un menú.

El searchView permite buscar en la lista actual como en el Api
![Screenshot_20221117-042422_Yu-Gi-Oh! Card Database](https://user-images.githubusercontent.com/115143423/202422636-cb21f88a-3850-4265-847b-cbdbb36ab27b.jpg)
![Screenshot_20221117-042403_Yu-Gi-Oh! Card Database](https://user-images.githubusercontent.com/115143423/202422641-f73f1690-1d6e-45d7-9872-30bbd50a41b3.jpg)
![Screenshot_20221117-042416_Yu-Gi-Oh! Card Database](https://user-images.githubusercontent.com/115143423/202422649-db312484-bd7c-4316-bbab-7a6bab70a517.jpg)
