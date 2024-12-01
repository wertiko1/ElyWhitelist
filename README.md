# ElyWhitelist - плагин для управления вайтлистом

Основной задачей при написании этого плагина было создание механизма добавления игроков в белый список без необходимости
использования интерфейсов типа RCON, команд, напрямую через сервер или других подобных инструментов. Это означает, что
добавление в белый список должно происходить через HTTP-запросы, что позволяет интегрировать систему в сторонние
приложения или другие серверы, которые могут взаимодействовать с плагином для обновления белого списка, не обращаясь к
серверу Minecraft напрямую.

## Команды

`/elywl reload` - перезагрузить плагин
`/elywl list` - показать вайтлист
`/elywl add` - добавить игрока в вайтлист
`/elywl remove` - удалить игрока из вайтлиста
`/elywl on` - включить вайтлист
`/elywl off` - выключить вайтлист

## Конфигурация

```yaml
# Сообщение
messages:
  notEnoughArguments: "<red>Недостаточно аргументов! Используйте /elywl <add|remove|on|off|list> [игрок]"
  playerAdded: "<green>Игрок %player% добавлен в белый список!"
  playerAlreadyInWhitelist: "<yellow>Игрок %player% уже в белом списке."
  playerRemoved: "<green>Игрок %player% удален из белого списка!"
  playerNotFound: "<yellow>Игрок %player% не найден в белом списке."
  whitelistEnabled: "<green>Белый список включен!"
  whitelistDisabled: "<red>Белый список отключен!"
  whitelistEmpty: "<yellow>Белый список пуст."
  whitelistList: "<green>Белый список: %players%"
  unknownCommand: "<red>Неизвестная команда! Используйте /elywl <add|remove|on|off|list>"
  notInWhitelist: "<red>Вас нет в вайтлисте!"

# Вкл/Выкл вайтлист
whitelistEnabled: true
noPermission: "Недостаточно прав!"

# Порт вашего сервера
port: 8080
# Токен для подтверждения запроса
token: "XXXXX"
```

## Права

`elywhitelist.use` - использование плагина
`elywhitelist.reload` - перезагрузка плагина