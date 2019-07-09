# SJEMA

Библиотека, позволяющая на основе XSD схемы получить view модель в формате JSON.
На основе view модели и XML документа строится JSON data модель.
На основе view модели и JSON data модели строится XML документ.
Могут существовать различные view модели описывающие один и тот же формат XML, что позволяет до-настраивать отображение сохраняя структуру данных XML.

## Структура view модели версии "1.0"

- **version** : string - версия документа. всегда "1.0"
- **targetNamespace** : string - целевое пространство имён
- **caption** : string - название
- **description** : string - описание
- **namespaces** : object - перечень пространств имён и соответствующих им префиксов
  - **[prefix]** : string - namespace. **prefix** - префикс
- **structure** : object - структура отображения
  - **[elementId]** : object - элемент. **elementId** - уникальный идентификатор элемента (в списке элементов)
    - **...** : структура зависит от потребностей использования
- **elements** : object - [элементы][view/elements]
  - **[id]** : object - [элемент][view/element]. **id** - уникальный идентификатор элемента (в списке элементов)
    - **path** : string - путь к узлу дерева XML
    - **createEmpty** : boolean - принудительное создание пути, даже если значение отсутствует
    - **typeId** : string - идентификатор типа - ссылка на тип (по умолчанию используется простой тип, с "base" : "string")
    - **caption** : string - название элемента отображаемое пользователю
    - **description** : string - расширенное описание поля
    - **required** : boolean - обязательность заполнения данных (по умолчанию false)
    - **repeatable** : object - описание повторяемости элемента
      - **path** : string - путь к повторяющемуся узлу дерева XML
      - **min** : number - минимальное количество элементов [0 - 1] (по умолчанию 0)
      - **max** : number - максимальное количество элементов [1 - 2^7] или unbounded (по умолчанию unbounded)
      - **caption** : string - заголовок повторяемого элемента с добавлением текущего индекса начиная с 1 (по умолчанию название головного элемента)
      - **addCaption** : string - название кнопки для добавления повторяемого элемента
      - **removeCaption** : string - название кнопки для удаления повторяемого элемента
    - **readOnly** : boolean - доступность элементов для редактирования (по умолчанию false)
    - **default** : string - значение элемента по умолчанию
- **types** : object - [типы][view/types]
  - **[id]** : object - [тип][view/type]. **id** - уникальный идентификатор типа, на основе имени типа
    - **description** : string - расширенное описание поля
    - **base** : string - базовый тип
    - **content** : object - описание содержимого
      - **mode** : string - режим обработки содержимого
        допустимые варианты значений:
          "sequence" - последовательно
          "choice" - выборочно, только один
      - **elementIds** : array - список идентификаторов элементов в порядке отображения
      - **xmlOrder** : array - список идентификаторов элементов в порядке требуемом форматом XML (по умолчанию используется порядок из elementIds)
    - **restriction** : object - описание ограничений
      - **enumeration** : object - список вариантов
        - **[value]** : string - человеко-читаемый текст. **value** - значение
      - **patterns** : array - список регулярных выражений с описаниями, одно из которых должно описать допустимое значение
        - **pattern** : string - регулярное выражение, которое определяет точную последовательность символов
        - **description** : string - описание выражения
      - **whitespace** : string - обработка пробелов
      - **length** : number - длина текста
      - **minLength** : number - минимальная длина текста
      - **maxLength** : number - максимальная длина текста
      - **minInclusive** : number - минимальное числовое значение включая указанное
      - **maxInclusive** : number - максимальное числовое значение включая указанное 
      - **minExclusive** : number - минимальное числовое значение исключая указанное
      - **maxExclusive** : number - максимальное числовое значение исключая указанное
      - **totalDigits** : number - точное число разрешенных цифр [1- 2^7]
      - **fractionDigits** : number - максимальное допустимое число десятичных знаков [0 - 2^7]
    - **widget** : object - [виджет][view/widget]
      - **name** : string - имя
      - **params** : object - перечень параметров зависит от имени
        - **...** : структур зависит от виджета
    - **mapperId** : string - идентификатор [мапера][view/mapper]
- **mappers** : object - [маперы][view/mappers]
  - **[id]** : object - [мапер][view/mapper]. **id** - название мапера.
    - **class** : string - класс мапера
      допустимые варианты значений:
        "XmlDateFormat" - форматированное значение даты и время в формате XML
        "DateFormat" - форматированное значение даты и время
    - **params** : object - перечень параметров зависит от класса
      - **[name]** : string - значение параметра. **name** - название параметра
- **validation** : object - [валидация][view/validation]
  - **[elementId]** :  array - список ошибок. **elementId** - идентификатор корневого элемента для применения правил
    - **rule** : string - правило поиска ошибочных вариантов
    - **message** : string - сообщение
    
## Подробное описание view модели

### view

Модель данных.

view модель описывает структуру модели данных, способ преобразования данных из JSON в XML и наоборот, способ отображения данных.

| Название            | Тип     | Обязательность | Описание                     | Значение по умолчанию | Условия                    |
| ------------------- | ------- | -------------- | ---------------------------- | --------------------- | -------------------------- |
| **version**         | string  | +              | версия документа             |                       | всегда имеет значение 1.0  |
| **caption**         | string  |                | название                     |                       |                            |
| **description**     | string  |                | описание                     |                       |                            |
| **targetNamespace** | string  | +              | целевое пространство имён    |                       |                            |
| **namespaces**      | object  |                | префиксы и пространства имён |                       |                            |
| **structure**       | object  | +              | структура отображения        |                       |                            |
| **elements**        | object  |                | [элементы][view/elements]    |                       |                            |
| **types**           | object  |                | [типы][view/types]           |                       |                            |
| **mappers**         | object  |                | [маперы][view/mappers]       |                       |                            |
| **validation**      | object  |                | [валидация][view/validation] |                       |                            |

### elements

Элементы.

Перечень элементов, обладающих уникальными (в рамках view) идентификаторами.

### element

Элемент.

Элемент описывает узел в модели данных.
Значение поля **path** как правило соответствует имени тега, который описывает элемент.

| Название        | Тип     | Обязательность | Описание                                                     | Значение по умолчанию            | Условия |
| --------------- | ------- | -------------- | ------------------------------------------------------------ | -------------------------------- | ------- |
| **path**        | string  | +              | путь к узлу дерева XML                                       |                                  |         |
| **createEmpty** | boolean |                | принудительное создание пути, даже если значение отсутствует | false                            |         |
| **typeId**      | string  |                | идентификатор типа - ссылка на тип                           | простой тип, с "base" : "string" |         |
| **caption**     | string  |                | название элемента отображаемое пользователю                  |                                  |         |
| **description** | string  |                | расширенное описание поля                                    |                                  |         |
| **required**    | boolean |                | обязательность заполнения данных                             | false                            |         |
| **repeatable**  | object  |                | описание повторяемости элемента                              |                                  |         |
| **readOnly**    | boolean |                | доступность элементов для редактирования                     | false                            |         |
| **default**     | string  |                | значение элемента по умолчанию                               |                                  |         |

### repeatable

Описание повторяемости элемента.

Наличие атрибута свидетельствует о повторяемости элемента.
Значение поля **path** как правило соответствует имени тега, который описывает повторяющийся элемент.

| Название          | Тип     | Обязательность | Описание                                             | Значение по умолчанию | Условия |
| ----------------- | ------- | -------------- | ---------------------------------------------------- | --------------------- | ------- |
| **path**          | string  | +              | путь к повторяющемуся узлу дерева XML                |                       |         |
| **min**           | number  |                | минимальное количество элементов                     | 0                     | 0 - 1   |
| **max**           | number  |                | максимальное количество элементов                    | unbounded             | 1 - 2^7 |
| **caption**       | string  |                | заголовок повторяемого элемента                      |                       |         |
| **addCaption**    | string  |                | название кнопки для добавления повторяемого элемента |                       |         |
| **removeCaption** | string  |                | название кнопки для удаления повторяемого элемента   |                       |         |

### types

Типы.

Перечень [типов][view/type], обладающих уникальными (в рамках view) идентификаторами.

### type

Тип.

Тип описывает правила которым должно соответствовать значение узла в data модели
Тип может иметь **режим**:
-  **simple** - простой
-  **complex** - комплексный
Режим управляется наличием атрибута content. При отсутствии атрибута тип является простым, при наличии комплексным.
Атрибут **base** принимает допустимые варианты значений:
- **boolean** - логический
- **number** - числовой
- **string** - строковый

| Название        | Тип     | Обязательность | Описание                            | Значение по умолчанию | Условия |
| --------------- | ------- | -------------- | ----------------------------------- | --------------------- | ------- |
| **description** | string  |                | расширенное описание поля           |                       |         |
| **base**        | string  |                | базовый тип                         |                       |         |
| **content**     | object  |                | описание содержимого                |                       |         |
| **restriction** | object  |                | описание ограничений                |                       |         |
| **widget**      | object  |                | виджет                              |                       |         |
| **mapperId**    | string  |                | идентификатор [мапера][view/mapper] |                       |         |

### content

Описание содержимого.

Наличие атрибута свидетельствует о том, что тип является комплексным.

| Название       | Тип     | Обязательность | Описание                                                          | Значение по умолчанию | Условия |
| -------------- | ------- | -------------- | ----------------------------------------------------------------- | --------------------- | ------- |
| **mode**       | string  | +              | режим обработки содержимого                                       |                       |         |
| **elementIds** | array   |                | список идентификаторов элементов в порядке отображения            |                       |         |
| **xmlOrder**   | array   |                | список идентификаторов элементов в порядке требуемом форматом XML | порядок из elementIds |         |

### restriction

Описание ограничений.

Атрибут **patterns** может содержать несколько выражений, объединённых через логическую операцию или, то есть, для успешной проверки должно положительно выполниться хотя бы одно из выражений.
Атрибут **whitespace** принимает допустимые варианты значений:
- **preserve** - сохранение всех white space символов
- **collapse** - замена всех white space символов, устранение повторяющихся пробелов на единичные
- **replace**  - замена всех white space символов


| Название           | Тип     | Обязательность | Описание                                        | Значение по умолчанию | Условия                  |
| ------------------ | ------- | -------------- | ----------------------------------------------- | --------------------- | ------------------------ |
| **enumeration**    | object  |                | список вариантов                                |                       |                          |
| **patterns**       | array   |                | список регулярных выражений с описаниями        |                       | для типа string          |
| **whitespace**     | string  |                | обработка пробелов                              |                       | для типа string          |
| **length**         | number  |                | длина текста                                    |                       | для типа string          |
| **minLength**      | number  |                | минимальная длина текста                        |                       | для типа string          |
| **maxLength**      | number  |                | максимальная длина текста                       |                       | для типа string          |
| **minInclusive**   | number  |                | минимальное значение включая указанное          |                       | для типа number          |
| **maxInclusive**   | number  |                | максимальное значение включая указанное         |                       | для типа number          |
| **minExclusive**   | number  |                | минимальное значение исключая указанное         |                       | для типа number          |
| **maxExclusive**   | number  |                | максимальное значение исключая указанное        |                       | для типа number          |
| **totalDigits**    | number  |                | точное число разрешенных цифр                   |                       | для типа number, 1- 2^7  |
| **fractionDigits** | number  |                | максимальное допустимое число десятичных знаков |                       | для типа number, 0 - 2^7 |

### pattern

Регулярное выражение.

Выражение определяет желаемую последовательность символов.

| Название        | Тип     | Обязательность | Описание             | Значение по умолчанию | Условия |
| --------------- | ------- | -------------- | -------------------- | --------------------- | ------- |
| **pattern**     | string  | +              | регулярное выражение |                       |         |
| **description** | string  |                | описание выражения   |                       |         |

### widget

Способ отображения данных.

Набор виджетов зависит от реализации интерфейсной части.

Структура атрибута зависит от виджета

| Название        | Тип     | Обязательность | Описание            | Значение по умолчанию | Условия          |
| --------------- | ------- | -------------- | ------------------- | --------------------- | ---------------- |
| **name**        | string  | +              | имя                 |                       |                  |
| **params**      | object  |                | описание параметров |                       | зависит от имени |


### mappers

Маперы.

Перечень маперов, обладающих уникальными (в рамках view) идентификаторами.

### mapper

Мапер.

| Название   | Тип     | Обязательность | Описание            | Значение по умолчанию | Условия           |
| ---------- | ------- | -------------- | ------------------- | --------------------- | ----------------- |
| **class**  | string  | +              | класс мапера        |                       |                   |
| **params** | object  |                | перечень параметров |                       | зависит от класса |

### validation

Валидация.

Валидация документа выполняется по элементно, в этой связи идентификатором валидатора является ссылка на проверяемый элемент.
Вычислить правило можно средствами библиотеки **JPath**.

### element validation

Валидация элемента.

Валидация осуществляется методом поиска ошибок. То есть, правило должно давать положительный ответ в случае ошибки.

| Название    | Тип     | Обязательность | Описание                           | Значение по умолчанию | Условия |
| ----------- | ------- | -------------- | ---------------------------------- | --------------------- | ------- |
| **rule**    | string  | +              | правило поиска ошибочных вариантов |                       |         |
| **message** | string  | +              | сообщение                          |                       |         |



[view]: https://github.com/anatolcom/sjema#view
[view/elements]: https://github.com/anatolcom/sjema#elements
[view/element]: https://github.com/anatolcom/sjema#element
[view/types]: https://github.com/anatolcom/sjema#types
[view/type]: https://github.com/anatolcom/sjema#type
[view/type]: https://github.com/anatolcom/sjema#mappers
[view/type]: https://github.com/anatolcom/sjema#mapper
[view/type]: https://github.com/anatolcom/sjema#validation 
[view/type]: https://github.com/anatolcom/sjema#element_validation

