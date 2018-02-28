for file in ./src/main/resources/i18n*.properties
do
  echo translating $file ...
  native2ascii -encoding UTF-8 $file $file
done