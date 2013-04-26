<!DOCTYPE html>
<html>
<head>
    <title>Spec</title>
</head>
<body>
<pre>

Этот сервис НЕ для хранения документов, только для убодства передачи.
Доступен интреактивный веб интерфейс и запускаемый на Вашем устройстве(компьютере) клиент,
позволяющий показывать коллегам Ваши файлы без кеширования их на сервере или, для вывода изображений сразу на телевизор.

Доступ к _распределённым_ данным. (NAS)

Передача (two-click):
+ text
+ load file
+ file from internet
+ load files
- link local directory
- link ftp directory
- webcam/photocam bridge
? move folder or link to webdav?
- синхронизация контактов (списком или одним, на почту или смской)
- перекидывание своего каталога в другой

Приём (two-click):
+ text
+ image
+ zip as directory
- selected as zip
- selected to email
- audio
- video
+ webDav
- ftp
- синхронизация (с двумя панелями и стрелочками)

- поиск в пределах сети

Интерфейс:
- one click interface
- touch/drag&drop interface
+ upload form
+ user directory browser (с индикатором загрузки)
+ load link
+ typed icon
+ drag & drop files

upload -> file/url/ftp
file -> zip -> download/to email/to new shared box/as short link
select -> zip -> download/to email/to (new) shared box/to ftp/to mms/to fotolab

Редактирование (three-click):
- zoomed image preview (быстрая сортировка и упорядочивание, привязка к местности и дате - только для linked directory)
- image editor (photo/presentation)
- audio/video trimmer/linker/splitter

java client (c UPNP пробросом, если доступно, на клиенте будет выбор источника и интерфейса данных)
android client вместо апплета скачивается файл установки на девайс
upload list of files (в конкретный каталог принимающего) upload, servlet, tcp sessions timeouts!!!
кеш на сервере (память(приоритет по времени) и диск(приоритет по количеству обращений и у upload передающих) )
java client для подключения каталога (возможно пустого, для получения)
WebDav
web media player / photo viewer (с возможностью редактировать в html5 и отправлять в сервис печати (обработанные файлы остаются у Вас дома!) (фотолаб,фоторум) )
Ctrl-V файлов и картинок (chrome api)
ссылка появляется раньше чем файл наЧинает грузиться - на пронимающем можно начинать закачку по событию (или вместо несуществующего ресурса возвращать страницу которая сама рефрешется (meta тегом или скриптом))

Может служить как презентация экрана или транслирования live видео (с перекодировками и проигрыванием стандартными средсвами html (или как-то ещё)) Тут могут быть любые доступные клиенты-трансляторы, iOS, flash, web-camers, и.т.п.


<a href="index.jsp">add file</a>
</pre>
</body>
</html>
