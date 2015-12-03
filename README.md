# trovaEntita
Sources (&amp; Tests) useful to find entities with a text, using regEx

this library builds a Map<String, Group> where:
- String is the text (group) that matches the used regEx
- Group is a Java Object that in-memory stores: the matching string, the number of occurrences in the original text, and all (list) the [start]:[end] offsets couples
