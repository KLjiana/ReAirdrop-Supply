# Airdrop Supply 空投
该模组需要配合数据包才能发挥全部效果，是为整合包作者提供的模组。

### 配置文件
所有的配置选项如下（均为整数）：
* **AIRDROP_SPREAD_RANGE** 空投箱散布最大距离，不小于10
* **AIRDROP_SPAWN_INTERVAL** 空投箱多久出现一次，以tick计算，不小于100
* **AIRDROP_DESPAWN_TIME** 空投箱多久后消失，以tick计算，不小于100
* **MEDIC_AIRDROP_WEIGHT** 医疗空投箱权重，不小于0
* **AMMO_AIRDROP_WEIGHT** 武器空投箱权重，不小于0
* **NO_AIRDROP_WEIGHT** 无空投箱权重，不小于0
> 请注意，`MEDIC_AIRDROP_WEIGHT`和`AMMO_AIRDROP_WEIGHT`总和请大于1。否则会造成崩溃。
* **BASIC_BASE_WEIGHT** 普通空投箱基础权重，不小于0
* **BASIC_MULTIPLE_WEIGHT** 普通空投箱加值权重，不小于0
* **MEDIUM_BASE_WEIGHT** 中级空投箱基础权重，不小于-10000
* **MEDIUM_MULTIPLE_WEIGHT** 普中级空投箱加值权重，不小于-10000
* **ADVANCED_BASE_WEIGHT** 高级空投箱基础权重，不小于-10000
* **ADVANCED_MULTIPLE_WEIGHT** 高级空投箱加值权重，不小于-10000
> 不同等级权重计算方式为 **Weight = BASE_WEIGHT(基础权重) + Day(天数) * MULTIPLE_WEIGHT(加值权重)**  
> 
> 权重小于1即该等级空投箱不会出现


### 数据包
本模组数据包需要包括六个战利品表，如何制作并使用数据包不在此赘述。

`airdrop_supply/medic_basic`对应的是普通医疗空投箱的战利品表   
`airdrop_supply/medic_medium`对应的是中级医疗空投箱的战利品表   
`airdrop_supply/medic_advanced`对应的是高级医疗空投箱的战利品表   
`airdrop_supply/ammo_basic`对应的是普通武器空投箱的战利品表   
`airdrop_supply/ammo_medium`对应的是中级武器空投箱的战利品表   
`airdrop_supply/ammo_advanced`对应的是高级武器空投箱的战利品表   

> 请限定战利品表的类型为宝藏箱`chest`。

`airdrop_supply/medic_basic`的简单范例如下，该箱子提供不超过4个苹果。
```json
{
  "type": "minecraft:chest",
  "pools": [
    {
      "rolls": {
        "type": "minecraft:uniform",
        "min": 1,
        "max": 4
      },
      "bonus_rolls": 0,
      "entries": [
        {
          "type": "minecraft:item",
          "name": "minecraft:apple"
        }
      ]
    }
  ]
}
```

`airdrop_supply/ammo_basic`的简单范例如下，该箱子提供2把铁剑，有可能提供一把钻石剑。
```json
{
  "type": "minecraft:chest",
  "pools": [
    {
      "rolls": {
        "type": "minecraft:uniform",
        "min": 0,
        "max": 1
      },
      "bonus_rolls": 0,
      "entries": [
        {
          "type": "minecraft:item",
          "name": "minecraft:diamond_sword"
        }
      ]
    },
    {
      "rolls": 2,
      "entries": [
        {
          "type": "minecraft:item",
          "name": "minecraft:iron_sword"
        }
      ]
    }
  ]
}
```

### 自定义通知
该模组的自定义通知可以通过自定义语言文件来修改，标准语言文件如下，`notification`开头即为通知。
```json
{
  "container.airdrop_supply.airdrop_supply": "空投补给箱",
  "notification.airdrop_supply.airdrop_arrive": "%4$s 的空投补给箱已到达 [%1$s]-[%2$s]-[%3$s] 。",
  "notification.airdrop_supply.airdrop_crash": "%1$s 的空投补给箱已坠毁。",
  "notification.airdrop_supply.airdrop_invalidate": "%4$s 的位于 [%1$s]-[%2$s]-[p%3$s] 的空投补给箱已超时失效。",
  "notification.airdrop_supply.airdrop_summoned": "%1$s's 呼叫了空投补给箱。请耐心等待。",
  "notification.airdrop_supply.airdrop_summoned_invalid_dimension": "%1$s 尝试在无法进行空投的维度呼叫补给箱。",
  "notification.airdrop_supply.airdrop_location_set": "%3$s 将补给箱空投位置固定在了 [%1$s]-[%2$s]。",
  "notification.airdrop_supply.airdrop_location_set_invalid_dimension": "%1$s 尝试在无法投递空投的维度设定固定空投位置。",
  "notification.airdrop_supply.airdrop_location_unset": "%1$s 取消了固定空投位置。",
  "notification.airdrop_supply.airdrop_location_unset_invalid_dimension": "无法在该维度取消固定空投位置。",
  "item.airdrop_supply.airdrop_pager": "空投补给箱呼叫器",
  "item.airdrop_supply.airdrop_location_fixer": "空投位置固定装置",
  "item.airdrop_supply.airdrop_location_canceller": "空投位置取消装置",
  "block.airdrop_supply.airdrop_supply": "空投补给箱"
}
```
* airdrop_arrive 补给箱投放成功通知。四个参数分别为补给箱 x, y, z 坐标和玩家名称。
* airdrop_crash 补给箱投放失败通知，这是由于随机到无补给箱造成的。参数为玩家名称。
* airdrop_invalidate 补给箱失效通知。四个参数分别为补给箱 x, y, z 坐标和玩家名称。
* airdrop_summoned 补给箱呼叫成功通知。参数为玩家名称。
* airdrop_summoned_invalid_dimension 补给箱呼叫失败通知，这是由于在非主世界呼叫补给箱造成的。主动呼叫的补给箱不会随机成无补给箱。参数为玩家名称。
* airdrop_location_set 空投位置设置成功通知。三个参数分别为补给箱 x, z 坐标和玩家名称。
* airdrop_location_set_invalid_dimension 空投位置设置失败通知。这是由于在非主世界设置空投位置造成的。参数为玩家名称。
* airdrop_location_unset 空投位置取消成功通知。参数为玩家名称。
* airdrop_location_unset_invalid_dimension 空投位置取消失败通知。这是由于在非主世界进行操作造成的。无参数。

### 空投召唤指令
格式;/airdrop x y z 空投等级 空投类型
> 只在主世界有效

# 更新 1.0.2
## 改动
* 定时的空投不会再落在树叶上

## 修复
* 空投不会再因砸中玩家而消失
* 空投不会再因掉落在雪地而消失

# 更新 1.1.0
* 新增空投等级
* 空投模型重置
* 新增召唤空投指令

# 更新 1.1.1
* 缩小空投箱碰撞体积