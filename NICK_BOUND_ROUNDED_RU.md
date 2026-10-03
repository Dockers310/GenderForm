# NickBound Rounded — что проверяем

## Клиент

Профиль игрока вычисляется по его нику. Для ника создаётся стабильный nickname-key:

`UUID.nameUUIDFromBytes("FemaleGenderMod:nick:" + nickname)`

Это значение одинаковое на Fabric-клиенте и в серверном плагине.

## Формы

`STANDARD` — исходная геометрия GenderForm.

`ROUNDED` — округлая геометрия на основе присланного `Natural-Breast-Prototype-5.0.0-natural.1+mc26.2-fabric.zip`. Прототип был только Fabric 26.2 и использовал собственные `RoundedBreastModelBox` / `RoundedBreastSurface`; в этой версии они встроены в выбор формы.

## Sync

После добавления формы в `Breasts.STREAM_CODEC` payload изменился: после `cleavage` появился VarInt формы:

- `0` = Standard
- `1` = Rounded

Серверный плагин проверяет этот идентификатор и пересылает payload дальше без изменения.
