package sri.diode

import diode.{Circuit, ModelR}
import sri.core.{ReactComponent, _}
import sri.core.all._
import scala.language.existentials
import scala.scalajs.js
import scala.scalajs.js.annotation.{ExposedJSMember, JSName, ScalaJSDefined}

object SriDiodeComponent {

  @ScalaJSDefined
  class Component[M <: AnyRef, S <: AnyRef] extends ReactComponent[Props[M, S], S] {


    override def componentWillMount(): Unit = {
      initialState(props.reader())
      unsubscribe = Some(props.circuit.subscribe(props.reader.asInstanceOf[ModelR[M, S]])(changeHandler))
    }

    def render() = props.compB(ModelProxy(props.reader, (action: AnyRef) => props.circuit.dispatch(action)))

    override def componentDidMount(): Unit = {
      isMounted = true
    }

    @JSName("sShouldComponentUpdate")
    @ExposedJSMember
    override def shouldComponentUpdate(nextProps: => Props[M, S], nextState: => S): Boolean = {
      state ne nextState
    }


    override def componentWillUnmount(): Unit = {
      isMounted = false
      unsubscribe.foreach(f => f())
      unsubscribe = None
    }

    private var unsubscribe = Option.empty[() => Unit]

    private var isMounted: Boolean = false


    private def changeHandler(cursor: ModelR[_, S]): Unit = {
      // modify state if we are mounted and state has actually changed
      if (isMounted && cursor =!= state)
        setState(cursor.value)
    }
  }

  case class Props[M <: AnyRef, S <: AnyRef](reader: ModelR[_, S], circuit: Circuit[M], compB: ModelProxy[S] => ReactElement)

  val ctor = js.constructorOf[Component[_, _]]

  def apply[M <: AnyRef, S <: AnyRef](reader: ModelR[_, S], circuit: Circuit[M], compB: ModelProxy[S] => ReactElement) = createElement(ctor.asInstanceOf[ReactTypedConstructor[Any, _]], Props(reader, circuit, compB))


}

