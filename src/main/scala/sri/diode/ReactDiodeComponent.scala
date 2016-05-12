package sri.diode

import diode.{ModelR, Circuit}
import sri.core.ReactComponent

import scala.scalajs.js.annotation.{ScalaJSDefined, ExposedJSMember, JSName}

@ScalaJSDefined
abstract class ReactDiodeComponent[M <: AnyRef, S <: AnyRef] extends ReactComponent[Unit, S] {

  val circuit: Circuit[M]

  val func: M => S

  initialState(circuit.zoom(func).value)

  override def componentWillMount(): Unit = {
    unsubscribe = Some(circuit.subscribe(state.asInstanceOf[ModelR[M, S]])(changeHandler))
  }


  override def componentDidMount(): Unit = {
    isMounted = true
  }


  @JSName("sShouldComponentUpdate")
  @ExposedJSMember
  override def shouldComponentUpdate(nextProps: => Unit, nextState: => S): Boolean = {
    state ne nextState
  }


  override def componentWillUnmount(): Unit = {
    unsubscribe.foreach(f => f())
    unsubscribe = None
  }

  private var unsubscribe = Option.empty[() => Unit]

  private var isMounted: Boolean = false


  private def changeHandler(cursor: ModelR[M, S]): Unit = {
    // modify state if we are mounted and state has actually changed
    if (isMounted && cursor =!= state)
      setState(cursor.value)
  }

}
