/*
 * Copyright (C) 2015 Ulrich Raab.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.ulrichraab.mvp;


/**
 * Base interface for all mvp presenters.
 * @param <T> The Ui this presenter coordinates.
 * @author Ulrich Raab
 */
public interface Presenter<T extends Ui> {

   /**
    * Attaches the specified user interface to this presenter.
    * @param ui The user interface to attach.
    */
   void onAttachUi (T ui);

   /**
    * Detaches the currently attached user interface.
    */
   void onDetachUi ();
}
