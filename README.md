# Android # Best practice Challenge for MVVM x RecyclerView

[活動介紹](https://medium.com/gdg-taipei/android-best-practice-challenge-for-mvvm-x-recyclerview-acd9e9ad0dae)

## 特色

- Kotlin 1.4
- `Activity` 用 view binding 取代 `findViewById`
- `RecyclerView`
  - 用 `ListAdapter` 做 `RecyclerView` adapter
  - List item 用 data binding
  - `ViewHolder` 有 `ViewModel` 的 reference
- 用 Dagger Hilt 做 dependency injection
- `@Parcelize`
- MVVM
  - 單一 Gradle module
  - 以 package 分開 UI、domain 和 data layer
  - `ViewModel` 用了 `SavedStateHandle` 儲存選用的排序方式，即使 `Activity` 被 kill 之後重開都能保持之前的設定
  - `ViewModel` 經 `LiveData` 通知 `Activity`；`Activity` 直接 call `ViewModel` method 通知 `ViewModel`
  - `ViewModel` call domain layer (`UseCase`)，domain layer 再 call data layer (`DataSource`) 以取得要顯示的內容

## `RecyclerView`

- 用了 `ListAdapter`，因為它會幫你在開 thread 做 diff，而且 `DiffUtil.ItemCallback` 要自己寫的 code 比 `DiffUtil.Callback` 少
- `ViewHolder` 內有 `bind` method，裏面負責更新 list item UI
- `ViewModel` 和 `LifecycleOwner` 都傳入去 `ViewHolder`，方便 data binding 不用再 call `executePendingBindings`； Click listener 用了
  data binding 直接 call `ViewHolder` 的 method，不用再傳 lambda 或者 callback 出去 `Activity`

## `Activity`

- 用 view binding 是因為本身只使用了 `findViewById`
- 因為這次用了 `Activity` 放 `RecyclerView`，所以不用擔心 configuration change 時會有 memory
  leak，可參考 [Tracing simple memory leak around RecyclerView using LeakCanary](https://yfujiki.medium.com/tracing-simple-memory-leak-around-recyclerview-using-leakcanary-927460532d53)
  - 留意 `Fragment` 用 view binding 或 data binding 都要在 `onDestroyView`
    時[清除 binding 的 reference](https://developer.android.com/topic/libraries/view-binding#fragments)
- `Activity` 主要負責處理 `LiveData` 的改動，例如 employee list 改變、需要顯示 toast 之類

## `ViewModel`

- 負責外露 `LiveData` 給 `Activity` 或 `Fragment`、接駁住 domain layer
- 因為顯示 toast 不會在 configuration change 後再顯示多次，所以用了 `OneOffEvent` 而非單純使用 `LiveData`

## Domain layer

- 這次因為沒有什麼特別功能，所以只是把 data layer 的東西左右交右手去 presentation layer

## Data layer

- 就是包住那個 `DummyEmployeeDataUtils`
- 用了 IO dispatcher 來模仿一般的情況
- 留意 `LiveData` 只在 presentation layer 用，其餘地方應該用 Coroutine

## Dependency injection & testing

- 用了 Dagger Hilt 做 dependency injection，因為方便寫 unit test
- 每個 layer 都有 interface 就是為了方便在 unit testing 可以 mock interface 而不是 mock concrete class
- Coroutine dispatcher 用了 Dagger inject 是為了方便 unit testing 時可以換做其他 dispatcher
- Unit testing 用了 MockK 和 Strikt，assertion library 用其他都沒大問題，都是個人喜好
- UI test 很久沒寫過，所以只寫了一個 test case 就算

## 其他

因為這個 challenge 比較簡單，所以就不搞到太複雜。但如果規模再大的 project 應該要把不同 layer 的 entity class 分開。 如果全部 layer 都共用 `Employee` data class
那只會愈來愈亂。例如加了不同的 annotation（Gson + Room 之類）再加上為了方便在 presentation layer 用再加上一些不會在 backend response 或 database 出現的
property。

---

# Panel Talk 回應

[Best practice Challenge for MVVM x RecyclerView Panel Talk](https://youtu.be/PFFsjwMPhp4?t=1994)

- 那種逐個 property 寫 `get(Employee::xxx).isEqualTo` 是 [Strikt](https://strikt.io/) 的一項功能。如果當中有一個 property
  錯誤時它會[清楚顯示那一部分有問題](https://strikt.io/wiki/traversing-subjects/) ，而不是放兩個 `toString` 要你自行比較。 但缺點是 assertion
  會寫得比單純用 `assertEquals` 更長。這次的示範因為 `Employee` 太簡單所以看起來比較無謂，但去到複雜的清況（例如那個 class 有很多 property 要檢查但沒有 override `equals`
  /`hashCode` 時就能充分發揮這種寫法的好處。
- 排序我沒有寫 unit test 是因為那段 code 本來是原本 repo 提供的，本來沒有寫 unit test 那我就不寫了。不過我現在補回了。
- 按照一般 Clean Architecture 的理解都會把 presentation、business logic、data I/O 分開。而我寫的 non-instrumentation test 都是 unit test 而不是
  integration test，所以 `MainViewModel` 只會有一些測試在 view 觸發的事件時 LiveData 會有什麼東西發射出來，而
  `ViewModel` 用到的 use case 都是 mock interface 而不是拿一個真實的 implementation 來測試。因為這個 project 太簡單，沒有特別的 logic，
  所以很多地方都是左手交右手，test case 都是做一些很簡單的東西。而每個 layer 之間都是用 interface 隔開都是方便寫 unit testing 可以 mock interface。
- Use case 直接轉用 lambda 寫雖然更簡潔，但應該會令 dependency injection 較難處理。
- 其實 view binding 和 data binding 我都有用，data binding 是在 view holder 用了。
- 那個 `OneOffEvent` 確實不是 thread safe，但因為現在穩定版仍未支援在 data binding 用 flow，所以維持用 `LiveData` 為主。現在有了
  [`repeatOnLifecycle`](https://medium.com/androiddevelopers/migrating-from-livedata-to-kotlins-flow-379292f419fb) 可以 轉用
  flow，所以現在已經刪走 `OneOffEvent`。
- `RecyclerView` adapter constructor 傳了 `ViewModel` 是因為這樣做比較方便。因為大部分情況都不會在不同頁面共用 adapter。如果有這個需求 的話可以改傳 lambda 或
  interface 處理 click listener。
