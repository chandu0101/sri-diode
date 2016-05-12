package sri.diode

import diode._
import sri.core.ReactElement
import scala.language.existentials

//https://github.com/ochrons/diode/blob/master/diode-react/src/main/scala/diode/react/ReactConnector.scala

/**
 * Wraps a model reader, dispatcher and React connector to be passed to React components
 * in props.
 */
case class ModelProxy[S](modelReader: ModelR[_, S], dispatch: AnyRef => _) {
  def value = modelReader()

  def apply() = modelReader()

  def zoom[T](f: S => T)(implicit feq: FastEq[_ >: T]) = ModelProxy(modelReader.zoom(f), dispatch)

  def wrap[T <: AnyRef](f: S => T)(compB: ModelProxy[T] => ReactElement)
                       (implicit feq: FastEq[_ >: T]): ReactElement = compB(zoom(f))

}

trait ReactConnector[M <: AnyRef] {
  circuit: Circuit[M] =>

  /**
   * Wraps a React component by providing it an instance of ModelProxy for easy access to the model and dispatcher.
   *
   * @param zoomFunc Function to retrieve relevant piece from the model
   * @param compB    Function that creates the wrapped component
   * @return The component returned by `compB`
   */
  def wrap[S <: AnyRef, C](zoomFunc: M => S)(compB: ModelProxy[S] => ReactElement)
                          (implicit feq: FastEq[_ >: S]): ReactElement = {
    wrap(circuit.zoom(zoomFunc))(compB)
  }

  /**
   * Wraps a React component by providing it an instance of ModelProxy for easy access to the model and dispatcher.
   *
   * @param modelReader A reader that returns the piece of model we are interested in
   * @param compB       Function that creates the wrapped component
   * @return The component returned by `compB`
   */
  def wrap[S <: AnyRef, C](modelReader: ModelR[_, S])(compB: ModelProxy[S] => ReactElement)
                          (implicit feq: FastEq[_ >: S]): ReactElement = {
    compB(ModelProxy(modelReader, (action: AnyRef) => circuit.dispatch(action)))
  }

  /**
   * Connects a React component into the Circuit by wrapping it in another component that listens to
   * relevant state changes and updates the wrapped component as needed.
   *
   * @param zoomFunc Function to retrieve relevant piece from the model
   * @param compB    Function that creates the wrapped component
   * @return A React component
   */
  def connect[S <: AnyRef](zoomFunc: M => S)(compB: ModelProxy[S] => ReactElement)
                          (implicit feq: FastEq[_ >: S]): ReactElement = {
    connect(circuit.zoom(zoomFunc))(compB)
  }

  /**
   * Connects a React component into the Circuit by wrapping it in another component that listens to
   * relevant state changes and updates the wrapped component as needed.
   *
   * @param modelReader A reader that returns the piece of model we are interested in
   * @param compB       Function that creates the wrapped component
   * @return A React component
   */
  def connect[S <: AnyRef](modelReader: ModelR[_, S])(compB: ModelProxy[S] => ReactElement)
                          (implicit feq: FastEq[_ >: S]): ReactElement = {
    SriDiodeComponent(modelReader, circuit, compB)
  }

}