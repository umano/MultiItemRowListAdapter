Android Multi Item Row ListAdapter
==================================
### An Easy Way to Make Your ListView's Look Amazing on Tablets
With the launch of the tablet version of Umano [Umano](http://umanoapp.com) [Android app](https://play.google.com/store/apps/details?id=com.sothree.umano) we decided to open-source another component, which allows to very quickly make your LiewViews and ListActivities looks amazing on tablets and phablets by placing multiple items of your ListAdapter in each row in a ListView. Umano Team <3 Open Source.

As seen in Umano ([http://umanoapp.com](http://umanoapp.com)):

![MultiItemRowListAdapter](https://raw.github.com/umano/MultiItemRowListAdapter/master/multiitem.png)

### Usage

All you need to do is wrapper your original ListAdapter using a `MultiItemRowListAdapter`:
```java
    int spacing = (int)getResources().getDimension(R.dimen.spacing);
    int itemsPerRow = getResources().getInteger(R.integer.items_per_row);
    MultiItemRowListAdapter wrapperAdapter = new MultiItemRowListAdapter(this, mAdapter, itemsPerRow, spacing);
    setListAdapter(wrapperAdapter);
```
As you can see the constructor for `MultiItemRowListAdapter` takes two parameters `itemsPerRow` and `spacing`. The first one is just the number of items from the original adapter to place on each row, and the second one is the cell spacing in pixels between the items. It's really convinient to specify the parameters in xml, so that you can easily vary the number of items per row on different screen orientations and sizes.
res/values/integers.xml - phone portrait (1 items per row)
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <integer name="items_per_row">1</integer>
</resources>
```
res/values-land/integers.xml - phone landscape (2 items per row)
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <integer name="items_per_row">1</integer>
</resources>
```
res/values-sw600/integers.xml - 7' tablet portrait (2 items per row)
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <integer name="items_per_row">2</integer>
</resources>
```
res/values-sw600-land/integers.xml - 7' tablet landscape (3 items per row)
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <integer name="items_per_row">3</integer>
</resources>
```

### Requrements
Tested on Android 2.2+

### Licence
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this work except in compliance with the License.
You may obtain a copy of the License in the LICENSE file, or at:

  [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
