# Unfolding
Create interactive thematic maps and geovisualizations.

* <http://unfoldingmaps.org/>
* <https://github.com/tillnagel/unfolding>

> **About this fork**
> This fork ([enkatsu/unfolding](https://github.com/enkatsu/unfolding)) builds Unfolding as a library for
> **Processing 4** (Java 17). The library code is the same as the original project; only the build and
> release process has changed. It requires Processing 4.0 or later and does not work with Processing 3.

## About
### Interaction Events
Unfolding enables you to rapidly create interactive maps. Basic interactions
such as Zoom & Pan are included. Other functionality such as Overview+Detail,
or multitouch gestures can be easily added.

### Data Visualization
Simply create geo-positioned markers to display data on a map. The visual style
can be adapted freely. The library supports any user-defined shapes, such as
points, lines, or polygons.

### Styled Maps
Unfolding is a tile-based map library. Map tiles can have various geographic
features, and come in all kind of styles. It comes with various map providers,
such as OpenStreetMap or TileMill.


## Download
### Processing 4
Download the latest release from <https://github.com/enkatsu/unfolding/releases>, then either

* drag and drop `Unfolding.pdex` onto the Processing editor, or
* unzip `Unfolding.zip` into the `libraries` folder of your sketchbook
  (e.g. `~/Documents/Processing/libraries/Unfolding`).

Restart Processing. The examples are listed under *Contributed Libraries* in *File > Examples*.

### Processing 3 and earlier
Get the original Unfolding from <http://unfoldingmaps.org/> or directly from here: http://unfoldingmaps.org/downloads


## Map Tile Providers
Several map tile services used by Unfolding have changed since the original release. This fork adapts to them:

* The default provider is now `EsriProvider.WorldGrayCanvas` (up to zoom level 16), as CARTO's Positron, the previous
  default, requires an API key.
* Some services require an API key, which you pass to the provider:

  | Provider | API key from |
  |---|---|
  | `CartoDB.*`, `OpenStreetMap.PositronMapProvider`, `OpenStreetMap.DarkMatterMapProvider` | [CARTO](https://carto.com/basemaps/apikey) |
  | `StamenMapProvider.*` (now hosted by Stadia Maps) | [Stadia Maps](https://stadiamaps.com/) |
  | `ThunderforestProvider.*` | [Thunderforest](https://www.thunderforest.com/) |
  | `MapBox.Streets`, `MapBox.Light`, `MapBox.Dark`, `MapBox.Satellite`, etc., `MapBox.StyleProvider` (own styles) | [Mapbox](https://www.mapbox.com/) |
  | `OpenWeatherProvider.*` (weather layers, e.g. to blend over a base map) | [OpenWeatherMap](https://openweathermap.org/) |

  ```java
  map = new UnfoldingMap(this, new StamenMapProvider.Toner("YOUR_API_KEY"));
  ```
* Tiles are requested with the User-Agent `Unfolding/<version> (+https://github.com/enkatsu/unfolding)`, which
  OpenStreetMap's tile servers require. To identify your own application, set `TileLoader.userAgent` before creating
  maps. When using `OpenStreetMap.OpenStreetMapProvider`, follow the
  [tile usage policy](https://operations.osmfoundation.org/policies/tiles/), e.g. show "© OpenStreetMap contributors".

Providers whose tile servers no longer exist are marked as deprecated: `AcetateProvider`, `ImmoScout`,
`MapQuestProvider`, `OpenMapSurferProvider`, `Yahoo`, `EsriProvider.DeLorme`, `MapBox.WorldLightProvider`,
`MapBox.ControlRoomProvider`, `MapBox.LacquerProvider`, `OpenStreetMap.OSMGrayProvider`,
`OpenStreetMap.CloudmadeProvider`, and `OpenWeatherProvider.PressureContour`. `Google` is deprecated as well, as it
accesses Google's map tiles via unofficial URLs, which Google's Terms of Service do not permit.

`MBTilesApp` needs the SQLite JDBC driver in the sketch's `code` folder (see `code/how-to-install-sqlite.txt`).


## Building from Source
Requires JDK 17. The Gradle wrapper downloads Gradle automatically.

```sh
./gradlew buildReleaseArtifacts        # creates release/Unfolding.zip, .pdex and .txt
./gradlew deployToProcessingSketchbook # installs the library into your Processing sketchbook
```

`deployToProcessingSketchbook` replaces any existing `libraries/Unfolding` folder in your sketchbook.

The Java examples in `examples/` and `examples-extern/` can be run with Gradle, e.g.

```sh
./gradlew runExample -Pexample=de.fhpotsdam.unfolding.examples.SimpleMapApp
```

### Releasing
1. Update `version` (an integer that must increase with each release) and `prettyVersion` in `release.properties`,
   and `VERSION` in `src/de/fhpotsdam/unfolding/UnfoldingMap.java` to the same `prettyVersion`.
2. Push a tag `v<prettyVersion>`, e.g. `git tag v0.9.96 && git push origin v0.9.96`.

GitHub Actions then builds the library and attaches `Unfolding.zip`, `Unfolding.pdex` and `Unfolding.txt` to a GitHub release.


## Credit
Developed at Interaction Design Lab, FH Potsdam, the HCI group, KU Leuven, and MIT Senseable City Labs.
See http://unfoldingmaps.org/contact.html for the full credits.

Processing 4 build and release by [Katsuya Endoh](https://enkatsu.org/).


## License

You may use Unfolding under the terms of the MIT License. See http://en.wikipedia.org/wiki/MIT_License for more information.


Copyright (C) 2015 Till Nagel, and contributors

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.


