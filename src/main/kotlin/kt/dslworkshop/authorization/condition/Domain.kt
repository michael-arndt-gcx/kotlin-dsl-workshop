package kt.dslworkshop.authorization.condition

interface Condition
data class Equals(val left: Any, val right: Any) : Condition
data class Conjunction(val left: Condition, val right: Condition) : Condition