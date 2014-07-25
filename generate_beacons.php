<?php
// require the Faker autoloader
require_once '/Users/aemreunal/Documents/GitHub/fzaninotto-Faker/src/autoload.php';

// use the factory to create a Faker\Generator instance
$faker = Faker\Factory::create();

// Parse command-line arguments
$options = getopt('n:');
if (count($options) == 0) {
  exit("Please specify number of beacons to generate. Ex: \"... -n100\" for 100 beacons.\n");
}

$NUM_BEACONS = intval($options["n"]);

$file = 'generated_beacons.txt';
$generatedBeacons = "";
$generateDescriptions = 1;

/*
 * Beacon JSON to generate:
{
  "uuid":"HelloWorld11111111111111111111111111", ->36 characters
  "major":"1", -> 1-4 characters
  "minor":"1", -> 1-4 characters
  "description":"testasd1" ->0-200 characters
}

 * As a list:
[{
  "uuid":"HelloWorld11111111111111111111111111",
  "major":"1",
  "minor":"1",
  "description":"testasd1"
},
{
  "uuid":"HelloWorld11111111111111111111111111",
  "major":"1",
  "minor":"1",
  "description":"testasd1"
}]
*/

if ($NUM_BEACONS > 1) {
  $generatedBeacons .= "[\n";
}

for ($i=0; $i < $NUM_BEACONS; $i++) {
  $generatedBeacons .= "{\n";

  $generatedBeacons .= "\"uuid\":\"" . $faker->uuid . "\",\n";
  $generatedBeacons .= "\"major\":\"" . $faker->numberBetween($min = 0, $max = ($NUM_BEACONS / 10)) . "\",\n";
  $generatedBeacons .= "\"minor\":\"" . $faker->numberBetween($min = 0, $max = ($NUM_BEACONS / 10)) . "\"";
  if($generateDescriptions == 1) {
    // Warning: Can create new line characters, which is unacceptable.
    $generatedBeacons .= ",\n";
    $generatedBeacons .= "\"description\":\"" . trim(preg_replace('/\s+/', ' ', ($faker->text($maxNbChars = 190)))) . "\"\n";
  } else {
    $generatedBeacons .= "\n";
  }

  $generatedBeacons .= "}\n";
  if ($NUM_BEACONS > 1 and ($i < $NUM_BEACONS - 1)) {
    $generatedBeacons .= ",\n";
  }
}

if ($NUM_BEACONS > 1) {
  $generatedBeacons .= "]\n";
}

file_put_contents($file, $generatedBeacons);

?>
